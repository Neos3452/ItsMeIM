package org.dragons.itsmeim.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dragons.itsmeim.common.Contact;
import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.Event;
import org.dragons.itsmeim.common.events.ProgramClosingEvent;
import org.dragons.itsmeim.controller.FatalModelError;
import org.dragons.itsmeim.model.exceptions.MulticastBindException;
import org.dragons.itsmeim.model.exceptions.ServerBindException;
import org.dragons.itsmeim.model.multicastprotocol.MulticastHandler;

/**
 * Model wysyła i odbiera wiadomości generowane przez użytkowników.
 */
public class Model
{
	/** Protokół obowiązujący w modelu. */
	static final Protocol protocol = new ConversationProtocol();
	/** Czas oczekiwania. */
	public static final long timeOut = 5;
	/** Jednostka czasu oczekiwania. */
	public static final TimeUnit timeOutUnit = TimeUnit.MINUTES;

	private final BlockingQueue<Event> inQueue;
	private final BlockingQueue<Event> outQueue;

	private final Map<ContactId, Connection> connections = new ConcurrentHashMap<ContactId, Connection>();

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private Server server;
	private Sender sender;
	private MulticastHandler multicast;
	/** Przypominacz obowiązujący w modelu. */
	private final ScheduledExecutorService timer = Executors
			.newSingleThreadScheduledExecutor();

	private Contact self = null;
	private Connection selfConnection = null;
	private List<InetAddress> candidates = new LinkedList<InetAddress>();
	private Set<Contact> contacts = new HashSet<Contact>();
	private List<String> displayedInterfaces = new LinkedList<String>();

	private Runnable load = new Runnable()
	{
		@Override
		public void run()
		{
			load();
		}
	};

	/**
	 * Tworzy model z podanymi kolejkami.
	 * 
	 * @param inQueue
	 * @param outQueue
	 */
	public Model(final BlockingQueue<Event> inQueue,
			final BlockingQueue<Event> outQueue)
	{
		this.inQueue = inQueue;
		this.outQueue = outQueue;
	}

	/**
	 * Ładuje pierwsze dane z dysku, lub generuje domyślne.
	 * 
	 * @return Lista dostępnych interfaców, lub pustą kolekcję jeżeli nie
	 *         potrzeba podejmować decyzji.
	 * @throws FatalModelError
	 */
	public Collection<String> preload() throws FatalModelError
	{
		final Enumeration<NetworkInterface> interfaces;
		try
		{
			interfaces = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException e)
		{
			/*
			 * Nie udało się pobrać interface'ów sieciowych zwracam pustą
			 * kolekcje.
			 */
			e.printStackTrace();
			return Collections.unmodifiableList(displayedInterfaces);
		}

		try
		{
			candidates.add(InetAddress.getLocalHost());
			displayedInterfaces.add("Auto - " + candidates.get(0));
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			/*
			 * Ignorujemy getLocaclHost() exception, ponieważ wymusimy na
			 * użytkowniku wybór interface'u.
			 */
		}
		while (interfaces.hasMoreElements())
		{
			final NetworkInterface netInterface = interfaces.nextElement();
			try
			{
				if (!netInterface.isLoopback() && netInterface.isUp())
				{
					final Enumeration<InetAddress> addresses = netInterface
							.getInetAddresses();
					while (addresses.hasMoreElements())
					{
						InetAddress address = addresses.nextElement();
						candidates.add(address);
						displayedInterfaces.add(""
								+ netInterface.getDisplayName() + " - "
								+ address.getHostAddress());
					}
				}
			}
			catch (SocketException e)
			{
				e.printStackTrace();
				/*
				 * Jeżeli nie uda się pobrać informacji o interface'sie to go
				 * ignorujemy.
				 */
			}
		}
		return Collections.unmodifiableList(displayedInterfaces);
	}

	/**
	 * Model startuje i uruchamia wszystkie potrzebne moduły.
	 */
	public void load()
	{
		if (self == null)
		{
			outQueue.add(new ProgramClosingEvent());
			throw new NullPointerException("Self contact nie może być null!");
		}
		server.start();
		multicast.start();
		sender.start();
	}

	/**
	 * Zakończenie modelu i zwolnienie wszystkich zasobów.
	 */
	public void close()
	{
		// inQueue.add(new ProgramClosingEvent());
		multicast.close();
		sender.interrupt();
		server.close();
		for (Connection con : connections.values())
		{
			if (con.getCurrentConnection() != null)
			{
				con.getCurrentConnection().close();
			}
		}
		executor.shutdown();
		timer.shutdown();
	}

	/**
	 * Inicjuje moduł, przypisuje do potrzebnych socketów i przygotowywuje do
	 * pracy.
	 * 
	 * @param self Kontakt jaki się zalogował.
	 * @param picked Interface, który został wybrany.
	 * @return Kontakty, które załadował model.
	 * @throws FatalModelError Jeżeli nastąpił nie naprawialny błąd w modelu.
	 */
	public Collection<Contact> init(final Contact self, final String picked)
			throws FatalModelError
	{
		if (self == null)
		{
			throw new NullPointerException("Self contact nie może być null!");
		}
		this.self = self;
		try
		{
			if (picked != null && displayedInterfaces != null
					&& displayedInterfaces.contains(picked))
			{
				int index = displayedInterfaces.indexOf(picked);
				displayedInterfaces = null; // mark for GC
				System.out.println("Picked " + candidates.get(index));
				this.selfConnection = new Connection(self.getId(),
						candidates.get(index), 0, null);
			}
			else
			{
				this.selfConnection = new Connection(self.getId(),
						InetAddress.getLocalHost(), 0, null);
			}
			candidates = null; // mark for GC
			server = new Server(executor, self.getId(), selfConnection,
					connections, timer, outQueue, inQueue);
			server.bind();
			sender = new Sender(executor, self.getId(), selfConnection,
					contacts, connections, timer, outQueue, inQueue);
			multicast = new MulticastHandler(self.getId(), selfConnection,
					connections, timer, outQueue, inQueue);
			multicast.bind();

			loadContacts();

		}
		catch (UnknownHostException | MulticastBindException
				| ServerBindException e)
		{
			e.printStackTrace();
			throw new FatalModelError(e);
		}
		return Collections.unmodifiableSet(contacts);
	}

	/**
	 * Startuje model.
	 */
	public void start()
	{
		executor.execute(load);
	}

	/**
	 * Ładuje kontakty.
	 */
	private void loadContacts()
	{
		Contact hey = new Contact(new ContactId("Me"), "Me");
		contacts.add(hey);
		hey = new Contact(new ContactId("Hey"), "Hey");
		contacts.add(hey);
		hey = new Contact(new ContactId("Just"), "Just");
		contacts.add(hey);
		// connections.put(hey.getId(), new Connection(hey.getId(),
		// InetAddress.getByName("192.168.5.250"), 4600));
	}
}
