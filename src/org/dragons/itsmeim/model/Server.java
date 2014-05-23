package org.dragons.itsmeim.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.ContactIsOnlineEvent;
import org.dragons.itsmeim.common.events.Event;
import org.dragons.itsmeim.model.event.ConnectionInformationEvent;
import org.dragons.itsmeim.model.event.ImOnlineEvent;
import org.dragons.itsmeim.model.exceptions.MessageNotConformingToProtocolException;
import org.dragons.itsmeim.model.exceptions.ServerBindException;

/**
 * Odbiera nowe połącznia i tworzy dla nich informacje oraz słuchaczy.
 */
class Server extends Thread
{
	private ServerSocket serverSocket;
	private final ExecutorService executor;
	private final ContactId self;
	private final Connection selfConnection;
	private final Map<ContactId, Connection> connections;
	private final ScheduledExecutorService timer;
	private final BlockingQueue<Event> outQueue;
	private final BlockingQueue<Event> inQueue;

	public Server(final ExecutorService executor, final ContactId self,
			final Connection selfConnection,
			final Map<ContactId, Connection> connections,
			final ScheduledExecutorService timer,
			final BlockingQueue<Event> outQueue,
			final BlockingQueue<Event> inQueue)
	{
		this.executor = executor;
		this.self = self;
		this.connections = connections;
		this.timer = timer;
		this.selfConnection = selfConnection;
		this.outQueue = outQueue;
		this.inQueue = inQueue;
	}

	/**
	 * Przypisuje server do socketu.
	 * 
	 * @throws ServerBindException Jeżeli przypisanie niepowiodło się.
	 */
	public void bind() throws ServerBindException
	{
		try
		{
			synchronized (selfConnection)
			{
				serverSocket = new ServerSocket(selfConnection.getPort(), 0,
						selfConnection.getAddress());
				System.out.println("Bound to:"
						+ serverSocket.getLocalSocketAddress());
				selfConnection.setPort(serverSocket.getLocalPort());
			}
		}
		catch (IOException e)
		{
			System.out.println("Cannot bind");
			e.printStackTrace();
			throw new ServerBindException(
					"Server nie mogł podłączyć się do socketu o porcie "
							+ selfConnection.getPort() + ".", e);
		}
	}

	/**
	 * Odbiera połączenie i je potwierdza.
	 */
	@Override
	public void run()
	{
		try
		{
			try
			{
				while (true)
				{
					final Socket socket = serverSocket.accept();
					System.out.println("" + socket.getInetAddress() + ":"
							+ socket.getPort());
					confirmConnection(socket);
				}
			}
			catch (IOException e)
			{
				/*
				 * Jeżeli wątek servera został przerwany(w trakcie oczekiania na
				 * połącznie) to musi on zamknąć socket i się zakończyć.
				 */
				// e.printStackTrace();
			}
		}
		finally
		{
			close();
		}
	}

	/**
	 * Potwierdza połącznie i ewentualnie tworzy jego instancje.
	 * 
	 * @param socket Socket na, którym otwarte jest połączenie.
	 */
	private void confirmConnection(final Socket socket)
	{
		final ActiveConnection activeConnection = new ActiveConnection(socket);
		try
		{
			socket.setSoTimeout(5000);
			activeConnection.initInput();
			System.out.println("receive handshake");
			final Object recievedObject = activeConnection.receiveObject();
			ConnectionInformationEvent connectionInformation = Model.protocol
					.processFirstMessage(recievedObject);
			Connection connection = connections.get(connectionInformation
					.getContactId());
			if (connection == null)
			{
				connection = createConnection(connectionInformation);
			}
			else
			{
				updateConnection(connection, connectionInformation);
			}
			activeConnection.initOutput();
			System.out.println("send handshake");
			synchronized (selfConnection)
			{
				activeConnection.sendObject(Model.protocol
						.generateFirstMessage(new ConnectionInformationEvent(
								self, selfConnection.getAddress(),
								selfConnection.getPort())));
			}
			socket.setSoTimeout((int) Model.timeOutUnit.toMillis(Model.timeOut));
			synchronized (connection)
			{
				connection.setCurrentConnection(activeConnection);
			}
			System.out.println("New receiver");
			final Receiver r = new Receiver(connection, activeConnection,
					timer, outQueue, inQueue);
			synchronized (executor)
			{
				executor.execute(r);
			}
		}
		catch (MessageNotConformingToProtocolException | ClassNotFoundException
				| IOException e)
		{
			/*
			 * Jeżeli wystąpił problem z połączeniem to je kończymy.
			 */
			e.printStackTrace();
			activeConnection.close();
		}

	}

	/** Zamyka moduł serverowy. */
	public void close()
	{
		if (serverSocket != null)
			try
			{
				serverSocket.close();
			}
			catch (IOException e)
			{
				/*
				 * Jeżeli zamknięcie Socketa Servera się nie powiodło to
				 * ignorujemy, nie będziemy się nim więcej zajmować.
				 */
				e.printStackTrace();
			}
	}

	/**
	 * Zwraca używany przez server Socket.
	 * 
	 * @return
	 */
	public ServerSocket getServerSocket()
	{
		return serverSocket;
	}

	/**
	 * Tworzy nowe połącznie dla kontaktu.
	 * 
	 * @param connectionInformation
	 * @return
	 */
	private Connection createConnection(
			final ConnectionInformationEvent connectionInformation)
	{
		final ScheduleTask task = new ScheduleTask(new Runnable()
		{
			@Override
			public void run()
			{
				inQueue.add(new ImOnlineEvent(connectionInformation
						.getContactId()));
			}
		});
		final Connection connection = new Connection(
				connectionInformation.getContactId(),
				connectionInformation.getAddress(),
				connectionInformation.getPort(), task);
		task.setScheduled(timer.scheduleAtFixedRate(task.getTask(),
				Model.timeOut, Model.timeOut, Model.timeOutUnit));
		outQueue.add(new ContactIsOnlineEvent(connectionInformation
				.getContactId()));
		connection.setState(1);
		connections.put(connectionInformation.getContactId(), connection);
		return connection;
	}

	/**
	 * Aktualizuje informacje o połączniu dla kontaktu.
	 * 
	 * @param connection
	 * @param connectionInformation
	 */
	private void updateConnection(final Connection connection,
			final ConnectionInformationEvent connectionInformation)
	{
		synchronized (connection)
		{
			if (!connection.getAddress().equals(
					connectionInformation.getAddress()))
			{
				if (connection.getCurrentConnection() != null)
				{
					connection.getCurrentConnection().close();
					connection.setCurrentConnection(null);
				}
				connection.setAddress(connectionInformation.getAddress());
			}
			if (!(connection.getPort() == connectionInformation.getPort()))
			{
				if (connection.getCurrentConnection() != null)
				{
					connection.getCurrentConnection().close();
					connection.setCurrentConnection(null);
				}
				connection.setPort(connectionInformation.getPort());
			}
			connection.getOnlineCheck().cancel();
			connection.getOnlineCheck().setScheduled(
					timer.scheduleAtFixedRate(connection.getOnlineCheck()
							.getTask(), Model.timeOut, Model.timeOut,
							Model.timeOutUnit));
			if (connection.getState() == 0)
			{
				outQueue.add(new ContactIsOnlineEvent(connectionInformation
						.getContactId()));
				connection.setState(1);
			}
		}
	}
}
