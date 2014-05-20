package pl.michal.debski.itsmeim.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.text.BadLocationException;

import pl.michal.debski.itsmeim.common.*;
import pl.michal.debski.itsmeim.common.events.*;
import pl.michal.debski.itsmeim.model.Model;
import pl.michal.debski.itsmeim.view.View;

/**
 * Kontroluje przepływ informacji między widokiem i modele, inicjuje początkowo
 * te moduły.
 * 
 * @author Michał Dębski
 */
public class Controller implements Runnable
{
	private final Map<Class<? extends Event>, ApplicationStrategy> strategyMap;
	private final BlockingQueue<Event> viewQueue;
	private final View view;
	// my wysylamy eventy.
	private final BlockingQueue<Event> modelOutQueue;
	// produkuje nam eventy.
	private final BlockingQueue<Event> modelInQueue;
	private final Model model;
	// wątek przypiszę się sam w momencie uruchomienia.
	private Thread viewWatcher;
	private final Thread modelWatcher = new Thread()
	{
		@Override
		public void run()
		{
			watchModel();
		}
	};

	/**
	 * 
	 * @param viewQueue
	 * @param view
	 * @param modelOutQueue
	 * @param modelInQueue
	 * @param model
	 * @throws NullPointerException
	 */
	public Controller(final BlockingQueue<Event> viewQueue, View view,
			final BlockingQueue<Event> modelOutQueue,
			final BlockingQueue<Event> modelInQueue, Model model)
	{
		this.viewQueue = viewQueue;
		this.view = view;
		this.modelOutQueue = modelOutQueue;
		this.modelInQueue = modelInQueue;
		this.model = model;
		strategyMap = loadStrategy();
	}

	@Override
	public void run()
	{
		viewWatcher = Thread.currentThread();
		init();
	}

	/**
	 * Inicjalizacja programu.
	 */
	private void init()
	{
		try
		{
			final Collection<String> interfaces = model.preload();
			view.init();
			view.login(interfaces);
			final Event event = viewQueue.take();
			if (event instanceof ProgramClosingEvent)
			{
				view.close();
				return;
			}
			final LogedInEvent login = (LogedInEvent) event;

			System.out.println("Loged as " + login.getContactId());

			final Collection<Contact> contacts = model.init(
					new Contact(login.getContactId(), login.getContactId()
							.getName()), login.getPicked());

			view.initContacts(contacts);
			view.start();
			modelWatcher.start();
			model.start();
			watchView();
		}
		catch (Throwable t)
		{
			/*
			 * Każdy error podczas inicjalizacji skutkuje zamknięciem programu.
			 */
			t.printStackTrace();
			view.close();
			View.fatalError(t.getMessage());
		}
	}

	/**
	 * Użytkownik wysłał widomość.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class MessageSendApplicationStrategy extends ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final MessageToEvent messageToEvent = (MessageToEvent) event;
			try
			{
				System.out
						.println("Message to: "
								+ messageToEvent.getContactId()
								+ "\n\""
								+ messageToEvent.getMessage()
										.getText(
												0,
												messageToEvent.getMessage()
														.getLength()) + "\"");
			}
			catch (BadLocationException e)
			{
				/*
				 * Ignorujemy, bo to nie ma znaczenia dla działania programu, to
				 * zwykły log.
				 */
				e.printStackTrace();
			}
			modelOutQueue.add(messageToEvent);
		}

	}

	/**
	 * Rozpoczęto konwersacje.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class InitateConversationApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final ConversationToEvent conversationToEvent = (ConversationToEvent) event;
			System.out.println("Init conversation with : "
					+ conversationToEvent.getContactId());
		}

	}

	/**
	 * Zakończono konwersacje.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class EndConversationApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final ConversationEndedEvent conversationEndedEvent = (ConversationEndedEvent) event;
			System.out.println("End conversation with : "
					+ conversationEndedEvent.getContactId());
		}
	}

	/**
	 * Odebrano wiadomość.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class MessageFromEventApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final MessageFromEvent messageFromEvent = (MessageFromEvent) event;
			try
			{
				System.out.println("Message from: "
						+ messageFromEvent.getContactId()
						+ "\n\""
						+ messageFromEvent.getMessage().getText(0,
								messageFromEvent.getMessage().getLength())
						+ "\"");
			}
			catch (BadLocationException e)
			{
				/*
				 * Ignorujemy, bo to nie ma znaczenia dla działania programu, to
				 * zwykły log.
				 */
				e.printStackTrace();
			}
			view.messageReceived(messageFromEvent);
		}

	}

	/**
	 * Nie udało wysłać się wiadomości.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class MessageNotSendEventApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final MessageNotSendEvent messageNotSendEvent = (MessageNotSendEvent) event;
			System.out.println("Message not send");
			view.messageNotSend(messageNotSendEvent);
		}

	}

	/**
	 * Kontakt jest dostępny.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class ContactIsOnlineApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final ContactIsOnlineEvent contactIsOnlineEvent = (ContactIsOnlineEvent) event;
			System.out.println("" + contactIsOnlineEvent.getContactId()
					+ " is online");
			view.contactWentOnline(contactIsOnlineEvent);
		}

	}

	/**
	 * Kontakt jest niedostępny.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class ContactIsOfflineApplicationStrategy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final ContactIsOfflineEvent contactIsOfflineEvent = (ContactIsOfflineEvent) event;
			System.out.println("" + contactIsOfflineEvent.getContactId()
					+ " is offline");
			view.contactWentOffline(contactIsOfflineEvent);
		}

	}

	/**
	 * Mówi modelowi i widokowi, żeby się zakończyli i kończy sam siebie;
	 */
	private void closeProgram()
	{
		System.out.println("Shutdown!");
		view.close();
		viewWatcher.interrupt();
		modelWatcher.interrupt();
		model.close();
	}

	/**
	 * Obserwuje kolejke widoku.
	 */
	private void watchView()
	{
		while (true)
		{
			try
			{
				final Event event = viewQueue.take();
				strategyMap.get(event.getClass()).process(event);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	/**
	 * Obserwuje kolejkę modelu.
	 */
	private void watchModel()
	{
		while (true)
		{
			try
			{
				final Event e = modelInQueue.take();
				strategyMap.get(e.getClass()).process(e);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	/**
	 * Przesyła widokowi znalezione kontakty.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class AvailableContactsApplicationStartegy extends
			ApplicationStrategy
	{

		@Override
		public void process(Event event)
		{
			final AvailableContactsEvent availableContactsEvent = (AvailableContactsEvent) event;
			view.newAvailableContactsForAdd(availableContactsEvent
					.getContacts());
		}

	}

	/**
	 * Bazowa klasa strategii.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private abstract class ApplicationStrategy
	{
		public abstract void process(Event event);
	}

	/**
	 * Wrzuca modelowi event do kolejki.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class SendToModelApplicationStrategy extends ApplicationStrategy
	{

		@Override
		public void process(final Event event)
		{
			modelOutQueue.add(event);

		}

	}

	/**
	 * Zamyka cały program.
	 * 
	 * @author Michał Dębski
	 * 
	 */
	private class CloseProgramApplicationStrategy extends ApplicationStrategy
	{

		@Override
		public void process(final Event event)
		{
			closeProgram();
		}

	}

	/**
	 * Tworzy strategię kontrollera.
	 * 
	 * @return Niemodyfikowalna mapa strategii.
	 */
	private Map<Class<? extends Event>, ApplicationStrategy> loadStrategy()
	{
		final Map<Class<? extends Event>, ApplicationStrategy> tempMap = new HashMap<Class<? extends Event>, ApplicationStrategy>();

		tempMap.put(MessageToEvent.class, new MessageSendApplicationStrategy());
		tempMap.put(MessageNotSendEvent.class,
				new MessageNotSendEventApplicationStrategy());
		tempMap.put(ConversationToEvent.class,
				new InitateConversationApplicationStrategy());
		tempMap.put(ConversationEndedEvent.class,
				new EndConversationApplicationStrategy());
		tempMap.put(ProgramClosingEvent.class,
				new CloseProgramApplicationStrategy());
		tempMap.put(MessageFromEvent.class,
				new MessageFromEventApplicationStrategy());
		tempMap.put(ContactIsOnlineEvent.class,
				new ContactIsOnlineApplicationStrategy());
		tempMap.put(ContactIsOfflineEvent.class,
				new ContactIsOfflineApplicationStrategy());
		// Poprostu wysyła do modelu, nie ma sensu niczego z tym robić.
		tempMap.put(LookupContactEvent.class,
				new SendToModelApplicationStrategy());
		tempMap.put(AvailableContactsEvent.class,
				new AvailableContactsApplicationStartegy());
		// Poprostu wysyła do modelu, nie ma sensu niczego z tym robić.
		tempMap.put(AddContactEvent.class, new SendToModelApplicationStrategy());
		return Collections.unmodifiableMap(tempMap);
	}
}
