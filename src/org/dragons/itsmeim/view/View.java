package org.dragons.itsmeim.view;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.dragons.itsmeim.common.Contact;
import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.*;
import org.dragons.itsmeim.controller.FatalViewError;
import org.dragons.itsmeim.view.exceptions.NoSuchContactViewException;

import static org.dragons.itsmeim.view.ViewContactState.*;

/**
 * Całe zarządzanie widokiem, wszystkimi oknami itd.
 * 
 * @see MainWindow
 * @see ConversationWindow
 */
public class View
{
	private final BlockingQueue<Event> blockQueue;
	private final MainWindow mainWindow = new MainWindow();
	private AddContactWindow addWindow = null;
	private LoginWindow loginWindow = null;
	/**
	 * Zwiera wszystkie aktualne kontakty oraz przypisane im okna konwersacji,
	 * jeżeli takiego okna jeszcze nie ma to zwiera null.
	 */
	private final Map<ContactId, ViewContact> contacts = new LinkedHashMap<ContactId, ViewContact>();
	private final Map<ContactId, ConversationWindow> conversationWindows = new LinkedHashMap<ContactId, ConversationWindow>();
	private final ConversationWindowListener conversationListener = new ConversationWindowListener()
	{
		@Override
		public void windowClosing(final ConversationWindow conversationWindow)
		{
			conversationWindow.setVisible(false);
			blockQueue.add(new ConversationEndedEvent(conversationWindow
					.getContact().getContactId()));
		}

		@Override
		public void sendMessage(final ViewContact contact,
				final StyledDocument message,
				final ConversationWindow conversationWindow)
		{
			blockQueue.add(new MessageToEvent(contact.getContactId(), Calendar
					.getInstance().getTime(), (DefaultStyledDocument) message));
		}
	};

	/**
	 * Konstruuje widok z głównym oknem i ustawia jego Listenera.
	 * 
	 * @param blockingQueue
	 * @throws FatalViewError
	 * @see MainWindowListener
	 */
	public View(final BlockingQueue<Event> blockingQueue)
	{
		blockQueue = blockingQueue;
	}

	/**
	 * Inicjalizuje view.
	 * 
	 * @throws FatalViewError Jeżeli wystąpił error niemożliwy do naprawy.
	 */
	public void init() throws FatalViewError
	{
		try
		{
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException | IllegalAccessException
				| InstantiationException | ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new FatalViewError(e);
		}
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				mainWindow.setMainWindowListener(new MainWindowListener()
				{
					@Override
					public void openConversationWindow(ViewContact contact,
							MainWindow mainWindow)
					{
						View.this.loadConversationWindow(contact).setVisible(
								true);
					}

					@Override
					public void mainWindowClosing(MainWindow mainWindow)
					{
						blockQueue.add(new ProgramClosingEvent());

					}

					@Override
					public void openAddContact(MainWindow mainWindow)
					{
						openAddContactWindow(mainWindow);
					}
				});
			}
		});
	}

	/**
	 * Informuje widok o kontaktach pasujących do tych wpisanych w dodawaniu.
	 * 
	 * @param contacts
	 */
	public void newAvailableContactsForAdd(final Contact[] contacts)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (addWindow != null)
				{
					addWindow.newAvailableContacts(contacts);
				}
			}
		});
	}

	/**
	 * View umożliwi użytkownikowi zalogowanie się. Jeżeli użytkonik zaloguje
	 * się to view wygeneruje event zalogowania, w przeciwnym przypadku event
	 * zakończenia programu.
	 * 
	 * @param interfaces Lista interface'ów do wyboru
	 */
	public void login(final Collection<String> interfaces)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (interfaces.isEmpty())
				{
					loginWindow = new LoginWindow();
				}
				else
				{
					loginWindow = new LoginWindow(interfaces);
				}
				loginWindow.setLoginWindowListener(new LoginWindowListener()
				{

					@Override
					public void logIn(final LoginWindow lw)
					{
						blockQueue.add(new LogedInEvent(new ContactId(lw
								.getName()), lw.getInterface()));
						loginWindow.dispose();
						loginWindow = null;
					}

					@Override
					public void exit(final LoginWindow lw)
					{
						blockQueue.add(new ProgramClosingEvent());
						loginWindow.dispose();
						loginWindow = null;
					}
				});
				loginWindow.setVisible(true);
			}
		});
	}

	/**
	 * Zamyka widok.
	 */
	public void close()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				for (ConversationWindow conversationWindow : conversationWindows
						.values())
				{
					if (conversationWindow != null)
					{
						conversationWindow.dispose();
					}
				}
				mainWindow.dispose();
			}
		});
	}

	/**
	 * Wyświetla Error, który spowodował zamknięcie programu, może zostać
	 * wywołany niezależnie od całego widoku.
	 * 
	 * @param errorMessage
	 */
	static public void fatalError(final String errorMessage)
	{
		JOptionPane.showMessageDialog(null, errorMessage, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Ustawia okno i wyświetla je.
	 */
	public void start()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				// mainWindow.loadContacts(conversationWindows.keySet());
				mainWindow.pack();
				mainWindow.setVisible(true);
			}
		});
	}

	/**
	 * Widok wyświetli otrzymaną wiadomość.
	 * 
	 * @param event
	 */
	public void messageReceived(final MessageFromEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final ViewContact contact;
				if (!hasContact(event.getContactId()))
				{
					contact = new ViewContact(event.getContactId(), ON, event
							.getContactId().getName());
				}
				else
				{
					contact = contacts.get(event.getContactId());
				}
				final ConversationWindow conversationWindow = View.this
						.loadConversationWindow(contact);
				conversationWindow.messageReceived(event.getMessage(),
						event.getTime());
				conversationWindow.setVisible(true);
			}
		});
	}

	/**
	 * Widok poinformuje użytkownika o nie wysłanej wiadomości.
	 * 
	 * @param event
	 */
	public void messageNotSend(final MessageNotSendEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final ViewContact contact;
				if (!hasContact(event.getContactId()))
				{
					contact = new ViewContact(event.getContactId(), ON, event
							.getContactId().getName());
				}
				else
				{
					contact = contacts.get(event.getContactId());
				}
				final ConversationWindow conversationWindow = loadConversationWindow(contact);
				conversationWindow.messageNotSend(event.getMessage()
						.getMessage(), event.getMessage().getTime());
				conversationWindow.setVisible(true);
			}
		});
	}

	/**
	 * Kontakt stał się dostępny.
	 * 
	 * @param event
	 */
	public void contactWentOnline(final ContactIsOnlineEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final ViewContact contact = contacts.get(event.getContactId());
				contact.setState(ON);
				mainWindow.updateContact(contact);
				new ContactChangedStatePopup(contact);
			}
		});
	}

	/**
	 * Kontakt stał się niedostępny.
	 * 
	 * @param event
	 */
	public void contactWentOffline(final ContactIsOfflineEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				final ViewContact contact = contacts.get(event.getContactId());
				contact.setState(OFF);
				mainWindow.updateContact(contact);
				new ContactChangedStatePopup(contact);
			}
		});
	}

	/**
	 * Ładuje początkowe kontakty.
	 * 
	 * @param contacts
	 */
	public void initContacts(final Iterable<Contact> contacts)
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				for (Contact contact : contacts)
				{
					conversationWindows.put(contact.getId(), null);
					View.this.contacts.put(contact.getId(), new ViewContact(
							contact.getId(), OFF, contact.getDisplayName()));
				}
				mainWindow.loadContacts(View.this.contacts.values());
			}
		});
	}

	/**
	 * Dodaje kontakt do widoku.
	 * 
	 * @param contact Dane kontaktu do dodania.
	 */
	private void addContact(final Contact contact)
	{
		if (contact == null)
		{
			throw new NullPointerException("Contact cannot be null!");
		}
		if (!conversationWindows.containsKey(contact))
		{
			System.out.println("Adding:" + contact.getId() + " displayName:"
					+ contact.getDisplayName());
			conversationWindows.put(contact.getId(), null);
			final ViewContact viewContact = new ViewContact(contact.getId(),
					OFF, contact.getDisplayName());
			contacts.put(contact.getId(), viewContact);
			mainWindow.addContact(viewContact);
			mainWindow.validate();
		}
	}

	/**
	 * Aktualizuje dane o kontakcie, jeżeli kontakt nie istnieje to nic nie
	 * robi.
	 * 
	 * @param contact
	 */
	@SuppressWarnings("unused")
	private void updateContact(final Contact contact)
	{
		if (contact == null)
		{
			throw new NullPointerException("Contact cannot be null!");
		}
		final ViewContact viewContact = contacts.get(contact.getId());
		if (viewContact != null)
		{
			viewContact.setDisplayName(contact.getDisplayName());
			mainWindow.updateContact(viewContact);
		}
	}

	/**
	 * Usuwa podany kontakt i zwraca go w aktualnym stanie.
	 * 
	 * @param contactId
	 * @return <ul>
	 *         <li><b>Contact</b> – w jakim stanie wiedział o nim View.</li>
	 *         <li><b>null</b> – jeżeli View nie wiedział o takim kontakcie</li>
	 *         </ul>
	 */
	@SuppressWarnings("unused")
	private Contact removeContact(final ContactId contactId)
	{
		if (contactId == null)
		{
			throw new NullPointerException("Contact cannot be null!");
		}
		final ViewContact rvalue = contacts.get(contactId);
		if (rvalue != null)
		{
			conversationWindows.remove(contactId);
			try
			{
				mainWindow.removeContact(rvalue);
				mainWindow.validate();
			}
			catch (NoSuchContactViewException e)
			{
				throw new RuntimeException(
						"Nie ma takiego kontaktu w głównym oknie!", e);
			}
		}
		return new Contact(rvalue.getContactId(), rvalue.getDisplayName());
	}

	/**
	 * Mówi czy View wie o danym kontakcie(nie sprawdza czy informacje się
	 * zgadzają!).
	 * 
	 * @param contactId
	 * @return <ul>
	 *         <li><b>true</b> – jeżeli View wie o tym kontakcie(dane nie muszą
	 *         się zgadzać!).</li>
	 *         <li><b>false</b> – jeżeli o nim nie wie.</li>
	 *         </ul>
	 */
	private boolean hasContact(final ContactId contactId)
	{
		if (contactId == null)
		{
			throw new NullPointerException("Contact cannot be null!");
		}
		return contacts.containsKey(contactId);
	}

	/**
	 * Wyświetla okno konwersacji danego kontaktu.
	 * 
	 * @param viewContact
	 * @return
	 */
	private ConversationWindow loadConversationWindow(
			final ViewContact viewContact)
	{
		ConversationWindow conversationWindow = conversationWindows
				.get(viewContact.getContactId());
		// ViewContact contact = contacts.get(c);
		if (conversationWindow == null)
		{
			conversationWindow = new ConversationWindow(viewContact);
			conversationWindow
					.setConversationWindowListener(conversationListener);
			conversationWindows.put(viewContact.getContactId(),
					conversationWindow);
		}
		if (!conversationWindow.isVisible())
		{
			blockQueue.add(new ConversationToEvent(viewContact.getContactId()));
		}
		return conversationWindow;
	}

	private void openAddContactWindow(final MainWindow mainWindow)
	{
		if (addWindow == null)
		{
			addWindow = new AddContactWindow();
			addWindow
					.setAddContactWindowListener(new AddContactWindowListener()
					{

						@Override
						public void inputChanged(
								final AddContactWindow addContactWindow)
						{
							blockQueue.add(new LookupContactEvent(
									addContactWindow.getContactInfo()));
						}

						@Override
						public void confirmed(
								final AddContactWindow addContactWindow)
						{
							blockQueue.add(new AddContactEvent(addContactWindow
									.getContactId(), addContactWindow
									.getContactInfo()));
							addContact(addContactWindow.getContactInfo());
							addContactWindow.dispose();
							addWindow = null;
							System.out.println("done");
						}

						@Override
						public void canceled(
								final AddContactWindow addContactWindow)
						{
							addContactWindow.dispose();
							addWindow = null;
						}
					});
		}
		addWindow.setVisible(true);
	}
}
