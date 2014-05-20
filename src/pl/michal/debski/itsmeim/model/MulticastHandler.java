package pl.michal.debski.itsmeim.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.ContactIsOnlineEvent;
import pl.michal.debski.itsmeim.common.events.Event;
import pl.michal.debski.itsmeim.model.event.ImOnlineEvent;
import pl.michal.debski.itsmeim.model.exceptions.MulticastBindException;
import pl.michal.debski.itsmeim.model.multicastprotocol.ContactInformation;

/**
 * Moduł multicastu, wysyła informacje o tym, że kontakt stał się dostępny i
 * odbiera taką informację od innych.
 * 
 * @author Michał Dębski
 */
public class MulticastHandler extends Thread
{
	private static final String MULTICAST_GROUP_NAME = "239.0.0.1";
	private static final int MULTICAST_PORT = 37822;
	private final ContactId self;
	private final Connection selfConnection;
	private final Map<ContactId, Connection> connections;
	private final ScheduledExecutorService timer;
	private final BlockingQueue<Event> outQueue;
	private final BlockingQueue<Event> inQueue;
	private final byte[] buffor = new byte[1500];
	private MulticastSocket socket;
	private DatagramSocket sendSocket;
	private InetAddress group;

	MulticastHandler(final ContactId self, final Connection selfConnection,
			final Map<ContactId, Connection> connections,
			final ScheduledExecutorService timer,
			final BlockingQueue<Event> outQueue,
			final BlockingQueue<Event> inQueue)
	{
		this.self = self;
		this.selfConnection = selfConnection;
		this.connections = connections;
		this.timer = timer;
		this.outQueue = outQueue;
		this.inQueue = inQueue;
	}

	/**
	 * Przypisuje moduł do socketu.
	 * 
	 * @throws MulticastBindException Jeżeli przypisanie się nie powiodło.
	 */
	public void bind() throws MulticastBindException
	{
		try
		{
			socket = new MulticastSocket(MULTICAST_PORT);
			sendSocket = new DatagramSocket();
		}
		catch (IOException e)
		{
			throw new MulticastBindException(
					"Multicast server nie mógł podłączyć się do socketów.", e);
		}
	}

	/** Wysyła informacje o dostępności i odbiera tąże od innych. */
	@Override
	public void run()
	{
		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try
		{
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(new ContactInformation(self
					.getName(), selfConnection.getAddress(), selfConnection
					.getPort()));
			byte[] array = byteArrayOutputStream.toByteArray();
			final InetAddress group = InetAddress
					.getByName(MULTICAST_GROUP_NAME);
			final DatagramPacket packet = new DatagramPacket(array,
					array.length, group, MULTICAST_PORT);
			System.out.println("Multicastuje connection info.");
			sendSocket.send(packet);
		}
		catch (IOException e)
		{
			/*
			 * Jeżeli był problem z wysłaniem wiadomości multicastowej to
			 * ignorujemy, spróbuje nasłuchiwać na wiadomości przychodzące.
			 */
			e.printStackTrace();
		}
		finally
		{
			if (objectOutputStream != null)
			{
				try
				{
					objectOutputStream.close();
				}
				catch (IOException e)
				{
					// jak się nie zamknie to zignorować
				}
			}
			if (byteArrayOutputStream != null)
			{
				try
				{
					byteArrayOutputStream.close();
				}
				catch (IOException e)
				{
					// jak się nie zamknie to zignorować
				}
			}
			sendSocket.close();
		}

		try
		{
			group = InetAddress.getByName(MULTICAST_GROUP_NAME);
			socket.joinGroup(group);
			final DatagramPacket packet = new DatagramPacket(buffor,
					buffor.length);

			ByteArrayInputStream byteArrayInputStream = null;
			ObjectInputStream objectInputStream = null;

			while (true)
			{
				socket.receive(packet);
				System.out.println("Odebralem pakiet z multicastu");
				try
				{
					byteArrayInputStream = new ByteArrayInputStream(
							packet.getData());
					objectInputStream = new ObjectInputStream(
							byteArrayInputStream);
					final ContactInformation info = (ContactInformation) objectInputStream
							.readObject();
					final ContactId id = new ContactId(info.getName());
					final Connection connection = connections.get(id);
					if (connection == null)
					{
						createConnection(id, info);
					}
					else
					{
						updateConnection(connection, info);
					}
					outQueue.add(new ContactIsOnlineEvent(id));
					inQueue.add(new ImOnlineEvent(id));
				}
				catch (ClassNotFoundException e)
				{
					/*
					 * Odebrano nieznaną wiadomość, ignoruje ją.
					 */
					e.printStackTrace();
				}
				finally
				{
					if (objectInputStream != null)
					{
						try
						{
							objectInputStream.close();
						}
						catch (IOException e)
						{
							// jak się nie zamknie to zignorować
						}
					}
					if (byteArrayInputStream != null)
					{
						try
						{
							byteArrayInputStream.close();
						}
						catch (IOException e)
						{
							// jak się nie zamknie to zignorować
						}
					}
				}
			}

		}
		catch (IOException e)
		{
			/*
			 * Watek odbiorcy wiadomości multicastowych został przerwany i ma
			 * zakończyć pracę, więc niech to zrobi.
			 */
			e.printStackTrace();
		}
		finally
		{
			close();
		}

	}

	/**
	 * Zamyka moduł.
	 */
	public void close()
	{
		if (group != null)
		{
			try
			{
				socket.leaveGroup(group);
			}
			catch (IOException e)
			{
				/*
				 * Jeżeli opuszczenie grupy multicastowej się nie powiodło to
				 * zamykanie i tak powinno kontynuować.
				 */
				e.printStackTrace();
			}
		}
		socket.close();
	}

	/**
	 * Jeżeli nie było połączenia przypisanego do tego konaktu to tworzy nowy i
	 * przypisuje.
	 * 
	 * @param id
	 * @param info
	 */
	private void createConnection(final ContactId id,
			final ContactInformation info)
	{
		ScheduleTask task = new ScheduleTask(new Runnable()
		{
			@Override
			public void run()
			{
				inQueue.add(new ImOnlineEvent(id));
			}
		});
		final Connection connection = new Connection(id, info.getAddress(),
				info.getPort(), task);
		connection.setState(1);
		connections.put(id, connection);
		task.setScheduled(timer.scheduleAtFixedRate(task.getTask(),
				Model.timeOut, Model.timeOut, Model.timeOutUnit));
	}

	/**
	 * Uaktualnia informacje o połączeniu tego kontaktu.
	 * 
	 * @param connection
	 * @param info
	 */
	private void updateConnection(final Connection connection,
			final ContactInformation info)
	{
		synchronized (connection)
		{
			if (!connection.getAddress().equals(info.getAddress()))
			{
				if (connection.getCurrentConnection() != null)
				{
					connection.getCurrentConnection().close();
					connection.setCurrentConnection(null);
				}
				connection.setAddress(info.getAddress());
			}
			if (!(connection.getPort() == info.getPort()))
			{
				if (connection.getCurrentConnection() != null)
				{
					connection.getCurrentConnection().close();
					connection.setCurrentConnection(null);
				}
				connection.setPort(info.getPort());
			}
			connection.setState(1);
			connection.getOnlineCheck().cancel();
			connection.getOnlineCheck().setScheduled(
					timer.scheduleAtFixedRate(connection.getOnlineCheck()
							.getTask(), Model.timeOut, Model.timeOut,
							Model.timeOutUnit));
		}
	}
}
