package pl.michal.debski.itsmeim.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import pl.michal.debski.itsmeim.common.Contact;
import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.AddContactEvent;
import pl.michal.debski.itsmeim.common.events.AvailableContactsEvent;
import pl.michal.debski.itsmeim.common.events.ContactEvent;
import pl.michal.debski.itsmeim.common.events.ContactIsOfflineEvent;
import pl.michal.debski.itsmeim.common.events.Event;
import pl.michal.debski.itsmeim.common.events.LookupContactEvent;
import pl.michal.debski.itsmeim.common.events.MessageEvent;
import pl.michal.debski.itsmeim.common.events.MessageNotSendEvent;
import pl.michal.debski.itsmeim.common.events.MessageToEvent;
import pl.michal.debski.itsmeim.model.event.ConnectionInformationEvent;
import pl.michal.debski.itsmeim.model.event.ImOnlineEvent;
import pl.michal.debski.itsmeim.model.exceptions.MessageNotConformingToProtocolException;

/**
 * Wątek wysyłający wiadomości na podstawie przychodzących zdarzeń.
 * 
 * @author Michał Dębski
 */
public class Sender extends Thread
{
	private final ExecutorService executor;
	private final ContactId self;
	private final Connection selfConnection;
	private final Set<Contact> contacts;
	private final Map<ContactId, Connection> connections;
	private final ScheduledExecutorService timer;
	private final BlockingQueue<Event> outQueue;
	private final BlockingQueue<Event> inQueue;

	Sender(final ExecutorService executor, final ContactId self,
			final Connection selfConnection, final Set<Contact> contacts,
			final Map<ContactId, Connection> connections,
			final ScheduledExecutorService timer,
			final BlockingQueue<Event> outQueue,
			final BlockingQueue<Event> inQueue)
	{
		this.executor = executor;
		this.self = self;
		this.selfConnection = selfConnection;
		this.contacts = contacts;
		this.connections = connections;
		this.timer = timer;
		this.outQueue = outQueue;
		this.inQueue = inQueue;
	}

	/**
	 * Wysyła wiadomości.
	 */
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Event event = inQueue.take();
				if (event instanceof ContactEvent)
				{
					if (event instanceof AddContactEvent)
					{
						synchronized (contacts)
						{
							contacts.add(((AddContactEvent) event)
									.getContactInfo());
						}
					}
					else
					{
						final ContactEvent message = (ContactEvent) event;
						final Connection connection = connections.get(message
								.getContactId());
						if (connection == null)
						{
							new Exception("Nie ma połączenia dla tego kontaktu")
									.printStackTrace();
							sendError(message);
							continue;
						}
						ActiveConnection current = null;
						synchronized (connection)
						{
							current = connection.getCurrentConnection();
						}
						if (current == null)
						{
							current = connect(connection, message);
							if (current == null)
								continue;
						}
						try
						{
							System.out.println("Sending: " + message);
							current.sendObject(Model.protocol.generateOutput(
									message, self));
						}
						catch (IOException e)
						{
							e.printStackTrace();
							if (current != null)
							{
								current.close();
							}
							if (event instanceof MessageEvent)
							{
								sendError(message);
							}
							if (event instanceof ImOnlineEvent)
							{
								synchronized (connection)
								{
									connection.getOnlineCheck().cancel();
									outQueue.add(new ContactIsOfflineEvent(
											connection.getContactId()));
								}
							}
						}
					}
				}
				else if (event instanceof LookupContactEvent)
				{
					outQueue.add(new AvailableContactsEvent(
							new Contact[] { ((LookupContactEvent) event)
									.getContact() }));
				}
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Zwraca zdarzenie o niepowodzeniu wysyłania.
	 * 
	 * @param message
	 */
	private void sendError(final ContactEvent message)
	{
		if (message instanceof MessageToEvent)
		{
			outQueue.add(new MessageNotSendEvent(message.getContactId(),
					(MessageToEvent) message));
		}
	}

	/**
	 * Tworzy nowe połącznie jeżeli nie było jeszcze nawiązane.
	 * 
	 * @param connection Informacje o połączeniu się do tego kontaktu.
	 * @param message Wysyłana wiadomość, zawiera informacje o kontakcie i celu
	 *            połączenia(do kogo i po co).
	 * @return
	 */
	private ActiveConnection connect(final Connection connection,
			final ContactEvent message)
	{
		System.out.println("No connection, creating new");
		ActiveConnection current = null;
		try
		{
			final Socket socket = new Socket();
			synchronized (connection)
			{
				socket.connect(new InetSocketAddress(connection.getAddress(),
						connection.getPort()), 5000);
			}
			System.out.println("new active con");
			current = new ActiveConnection(socket);

			current.initOutput();
			System.out.println("send handshake");
			synchronized (selfConnection)
			{
				current.sendObject(Model.protocol
						.generateFirstMessage(new ConnectionInformationEvent(
								self, selfConnection.getAddress(),
								selfConnection.getPort())));
			}
			socket.setSoTimeout(5000);
			current.initInput();
			System.out.println("receive handshake");
			final Object recievedObject = current.receiveObject();
			if (!Model.protocol.processFirstMessage(recievedObject)
					.getContactId().equals(message.getContactId()))
			{

				System.out.println("wrong contact");
				current.close();
				current = null;
			}
			socket.setSoTimeout((int) Model.timeOutUnit.toMillis(Model.timeOut));
			synchronized (connection)
			{
				connection.setCurrentConnection(current);
			}
			System.out.println("New receiver");
			final Receiver reciever = new Receiver(connection, current, timer,
					outQueue, inQueue);
			synchronized (executor)
			{
				executor.execute(reciever);
			}

		}
		catch (ClassNotFoundException | MessageNotConformingToProtocolException
				| IOException e)
		{
			e.printStackTrace();
			if (current != null)
			{
				current.close();
				current = null;
			}
			if (message instanceof ImOnlineEvent)
			{
				synchronized (connection)
				{
					connection.getOnlineCheck().cancel();
					outQueue.add(new ContactIsOfflineEvent(connection
							.getContactId()));
				}
			}
			sendError(message);
		}
		return current;
	}
}
