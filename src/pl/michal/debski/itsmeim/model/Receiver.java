package pl.michal.debski.itsmeim.model;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.*;
import pl.michal.debski.itsmeim.model.event.ConversationKeepAliveEvent;
import pl.michal.debski.itsmeim.model.exceptions.MessageNotConformingToProtocolException;

/**
 * Wątek odbierający wiadomości z socketu.
 * 
 * @author Michał Dębski
 */
class Receiver implements Runnable
{
	private final Connection connection;
	private final ActiveConnection currentConnection;
	private final ScheduledExecutorService timer;
	private final BlockingQueue<Event> outQueue;
	private final BlockingQueue<Event> inQueue;

	Receiver(final Connection who, final ActiveConnection currentConnection,
			final ScheduledExecutorService timer,
			final BlockingQueue<Event> outQueue,
			final BlockingQueue<Event> inQueue)
	{
		connection = who;
		this.currentConnection = currentConnection;
		this.timer = timer;
		this.outQueue = outQueue;
		this.inQueue = inQueue;
	}

	/**
	 * Odbiera wiadomości i generuję odpowiednie zdarzenia.
	 */
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				final Object recievedObject = currentConnection.receiveObject();
				synchronized (connection)
				{
					final Event recievedEvent = Model.protocol.processInput(
							recievedObject, connection.getContactId());
					if (!(recievedEvent instanceof ConversationKeepAliveEvent))
					{
						if (recievedEvent instanceof ContactIsOnlineEvent)
						{
							if (connection.getState() == 0)
							{
								connection.setState(1);
								outQueue.add(recievedEvent);
							}
							break;
						}
						connection.getOnlineCheck().cancel();
						connection.getOnlineCheck().setScheduled(
								timer.scheduleAtFixedRate(connection
										.getOnlineCheck().getTask(),
										Model.timeOut, Model.timeOut,
										Model.timeOutUnit));
						outQueue.add(recievedEvent);
						inQueue.add(new ConversationKeepAliveEvent(
								new ContactId(connection.getContactId())));
					}
				}
			}
		}
		catch (MessageNotConformingToProtocolException | IOException
				| ClassNotFoundException e)
		{
			/*
			 * Problem z połączeniem, lub powinno zostać zamknięte, zamykam
			 * odbiorcę i kończę.
			 */
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Receiver closing");
			currentConnection.close();
			synchronized (connection)
			{
				connection.setCurrentConnection(null);
			}
		}
	}

	/**
	 * Zwraca połączenie, na którym pracuje obiorca.
	 * 
	 * @return
	 */
	public Connection getConnection()
	{
		return connection;
	}
}
