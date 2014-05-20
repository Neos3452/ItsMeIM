package pl.michal.debski.itsmeim.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pl.michal.debski.itsmeim.view.exceptions.NoSuchContactViewException;
import static pl.michal.debski.itsmeim.view.ViewContactState.*;

/**
 * Główne okno.<!-- --> Pokazuje dostępne kontakty, wie kiedy zamknąć program
 * lub otworzyć okno konwersacji.
 * <p>
 * Do obsługi zdarzeń okna służy {@link MainWindowListener}.
 * </p>
 * <p>
 * <b>Uwaga:</b> Obiekty widoku nie są "Thread safe".
 * </p>
 * 
 * @author Michał Dębski
 */
class MainWindow
{

	final private JFrame window;

	private MainWindowListener listener;
	private final Map<ViewContact, ContactPanel> contactPanels = new HashMap<ViewContact, MainWindow.ContactPanel>();
	private final MouseListener mouseListener = new MouseAdapter()
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				final ViewContact contact = ((ContactPanel) e.getSource())
						.getContact();
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						listener.openConversationWindow(contact,
								MainWindow.this);
					}
				});
			}
		}
	};
	private final WindowListener windowListener = new WindowAdapter()
	{
		@Override
		public void windowClosing(WindowEvent arg0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.mainWindowClosing(MainWindow.this);
				}
			});
		}
	};

	private final MenuBar menuBar = new MenuBar();
	private final Menu contactsMenu = new Menu("Contacts");
	private final MenuItem addContactsMenu = new MenuItem("Add");

	private final JPanel contactsListPanel = new JPanel();
	private final JScrollPane contactsListScrollPane = new JScrollPane(
			contactsListPanel);
	private final BoxLayout contactListLayout = new BoxLayout(
			contactsListPanel, BoxLayout.Y_AXIS);

	/**
	 * Konstruuje Główne Okno używając standardowych rozmiarów.
	 * <p>
	 * Tworzy JScrollPane wypełniający cala powierzchnie z JPanel w środku do
	 * umieszczania kontaktów.
	 * </p>
	 * 
	 * @throws HeadlessException
	 */
	public MainWindow()
	{
		window = new JFrame("It's Me!");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setMinimumSize(new Dimension(255, 100));

		addContactsMenu.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						listener.openAddContact(MainWindow.this);
					}
				});
			}
		});
		contactsMenu.add(addContactsMenu);
		menuBar.add(contactsMenu);
		window.setMenuBar(menuBar);

		contactsListScrollPane.setBorder(BorderFactory.createEmptyBorder());
		contactsListScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contactsListScrollPane.getViewport().addChangeListener(
				new ChangeListener()
				{

					@Override
					public void stateChanged(ChangeEvent arg0)
					{
						contactsListPanel.validate();
					}
				});
		contactsListPanel.setBackground(Color.YELLOW);
		contactsListPanel.setLayout(contactListLayout);
		window.add(contactsListScrollPane, BorderLayout.CENTER);
		window.setLocationRelativeTo(null);
	}

	/**
	 * Dodaje kontakt do aktualnie wyświetlanych.
	 * 
	 * @param contact
	 */
	public void addContact(final ViewContact contact)
	{
		final ContactPanel contactPanel = new ContactPanel(contact);
		contactPanels.put(contact, contactPanel);
		if (mouseListener != null)
		{
			contactPanel.addMouseListener(mouseListener);
		}
		contactsListPanel.add(contactPanel);
	}

	/**
	 * Aktualizuje dane kontaktu.
	 * 
	 * @param contact
	 */
	public void updateContact(final ViewContact contact)
	{
		final ContactPanel contactPanel = contactPanels.get(contact);
		contactPanel.setTitle(contact.getDisplayName());
		contactPanel.repaint();

	}

	/**
	 * Usuwa kontakt z wyświetlanej listy.
	 * 
	 * @param contact
	 * @throws NoSuchContactViewException Jeżeli kontakt nie został wcześniej
	 *             dodany.
	 */
	public void removeContact(final ViewContact contact)
			throws NoSuchContactViewException
	{
		final ContactPanel contactPanel = contactPanels.remove(contact);
		if (contactPanel == null)
		{
			throw new NoSuchContactViewException(
					"W oknie glownym nie ma takiego kontaktu");
		}
		contactsListPanel.remove(contactPanel);
	}

	/**
	 * Ładuje kontakty.
	 * 
	 * @param contacts
	 */
	public void loadContacts(final Iterable<ViewContact> contacts)
	{
		for (final ViewContact contact : contacts)
		{
			addContact(contact);
		}
	}

	/**
	 * Ustawia nowego Listenera. Jeżeli wcześniej był ustawiony Listener to
	 * najpierw go usuwa.
	 * 
	 * @param mainWindowListener
	 */
	public void setMainWindowListener(
			final MainWindowListener mainWindowListener)
	{
		if (listener != null)
		{
			removeMainWindowListener();
		}
		listener = mainWindowListener;
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(windowListener);
		for (final ContactPanel contactPanel : contactPanels.values())
		{
			contactPanel.addMouseListener(mouseListener);
		}
	}

	/**
	 * Usuwa aktualnego Listenera.
	 */
	public void removeMainWindowListener()
	{
		if (listener != null)
		{
			listener = null;
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.removeWindowListener(windowListener);
			for (final ContactPanel contactPanel : contactPanels.values())
			{
				contactPanel.removeMouseListener(mouseListener);
			}
		}
	}

	/**
	 * Zwraca aktualnego Listenera.
	 */
	public MainWindowListener getMainWindowListener()
	{
		return listener;
	}

	/**
	 * Usuwa okno.
	 */
	public void dispose()
	{
		window.dispose();
	}

	/**
	 * Ustawia preferowany rozmiar okna.
	 * 
	 * @param d
	 */
	public void setPreferredSize(final Dimension d)
	{
		window.setPreferredSize(d);
	}

	/**
	 * Ustawia lewy górny róg okna w wybranym punkcie.
	 * 
	 * @param p
	 */
	public void setLocation(final Point p)
	{
		window.setLocation(p);
	}

	/**
	 * Ustawia okno w podanym prostokącie.
	 * 
	 * @param r
	 */
	public void setBounds(final Rectangle r)
	{
		window.setBounds(r);
	}

	/**
	 * Zmienia widoczność okna.
	 * 
	 * @param b
	 */
	public void setVisible(final boolean b)
	{
		window.setVisible(b);
	}

	/**
	 * Ustawia okno tak, żeby wszystkie elementy się mieściły.
	 */
	public void pack()
	{
		window.pack();
	}

	/**
	 * Ustawia ponownie elementy w oknie(najlepiej używać przy dodaniu/usuwaniu
	 * kontaktów).
	 */
	public void validate()
	{
		window.validate();
	}

	/**
	 * Panel do wyświetlania kontaktów w oknie głównym.
	 * 
	 * @author Michał Dębski
	 */
	private class ContactPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private final ViewContact contact;
		private final int height = 30;
		private final JLabel label;
		private final JLabel state = new JLabel();

		/**
		 * Towrzy domyślny panel z tytułem jako displayName kontaktu.
		 * 
		 * @param contact
		 */
		public ContactPanel(final ViewContact contact)
		{
			super();
			this.contact = contact;
			label = new JLabel(contact.getDisplayName());
			this.setOpaque(false);
			this.setMinimumSize(new Dimension(MainWindow.this.window
					.getMinimumSize().width, height));
			this.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			state.setMinimumSize(new Dimension(30, 30));
			this.add(state, BorderLayout.WEST);
			this.add(label, BorderLayout.CENTER);
			this.setAlignmentX(LEFT_ALIGNMENT);
		}

		/**
		 * Ustawia wyświetlaną nazwę.
		 * 
		 * @param title
		 */
		public void setTitle(final String title)
		{
			label.setText(title);
		}

		/**
		 * Zwraca wyświetlany kontakt.
		 * 
		 * @return
		 */
		public ViewContact getContact()
		{
			return contact;
		}

		/** Rysuje 'kółka' dostępności. */
		@Override
		public void paint(final Graphics g)
		{
			super.paint(g);
			if (contact.getState().equals(ON))
			{
				g.setColor(Color.GREEN);
			}
			else
			{
				g.setColor(Color.RED);
			}
			g.fillOval(5, 5, 20, 20);
		}
	}
}