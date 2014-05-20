package pl.michal.debski.itsmeim.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.michal.debski.itsmeim.common.Contact;
import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Okno służące do dodawania kontaktów.
 * 
 * @author Michał Dębski
 *         <p>
 *         <b>Uwaga:</b> Obiekty widoku nie są "Thread safe".
 *         </p>
 */
public class AddContactWindow
{
	private AddContactWindowListener listener = null;
	final private JFrame window = new JFrame("Add Contact");
	final private JPanel up = new JPanel();
	final private JPanel down = new JPanel();
	final private JButton add = new JButton("Add");;
	final private JButton cancel = new JButton("Cancel");
	final private JTextField nameField = new JTextField();
	final private JTextField displayedNameField = new JTextField();

	/**
	 * Słucha na zamknięcie ona i działa jakby został naciśnięty przycisk
	 * cancel.
	 */
	private WindowListener windowListener = new WindowAdapter()
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.canceled(AddContactWindow.this);
				}
			});
		}
	};
	/**
	 * Naciśnięcie przycisku "Add".
	 */
	private ActionListener addListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.confirmed(AddContactWindow.this);
				}
			});
		}
	};
	/**
	 * Naciśnięcie przycisku "Cancel".
	 */
	private ActionListener cancelListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.canceled(AddContactWindow.this);
				}
			});
		}
	};
	/**
	 * Nasłuchuje na jakiekolwiek zmiany tekstu, do sprawdzania na bierząco
	 * poprawności danych.
	 */
	private DocumentListener changeListener = new DocumentListener()
	{
		@Override
		public void removeUpdate(DocumentEvent arg0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					add.setEnabled(false);
					listener.inputChanged(AddContactWindow.this);
				}
			});
		}

		@Override
		public void insertUpdate(DocumentEvent arg0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					add.setEnabled(false);
					listener.inputChanged(AddContactWindow.this);
				}
			});
		}

		@Override
		public void changedUpdate(DocumentEvent arg0)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					add.setEnabled(false);
					listener.inputChanged(AddContactWindow.this);
				}
			});
		}
	};

	{
		add.setEnabled(false);

		up.setLayout(new BoxLayout(up, BoxLayout.Y_AXIS));
		up.add(new JLabel("Name:", SwingConstants.RIGHT));
		up.add(nameField);
		up.add(new JLabel("Displayed name:", SwingConstants.RIGHT));
		up.add(displayedNameField);

		down.add(add, BorderLayout.WEST);
		down.add(cancel, BorderLayout.EAST);

		window.setMinimumSize(new Dimension(down.getMinimumSize().width, up
				.getMinimumSize().height + down.getMinimumSize().height));
		window.add(up);
		window.add(down, BorderLayout.SOUTH);
		window.pack();
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Zmienia widzialność okna.
	 * 
	 * @param b
	 */
	public void setVisible(final boolean b)
	{
		window.setVisible(b);
	}

	/** Usuwa okno. */
	public void dispose()
	{
		window.dispose();
	}

	/**
	 * Zwraca String wpisany w polu "name".
	 * 
	 * @return
	 */
	public String getName()
	{
		return nameField.getText();
	}

	/**
	 * Zwraca String wpisany w polu "Display Name".
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return displayedNameField.getText();
	}

	/**
	 * Generuje kontakt na podstawie wpisnych danych.
	 * 
	 * @return
	 */
	public Contact getContactInfo()
	{
		return new Contact(getContactId(), getDisplayName());
	}

	/**
	 * Generuje id kontaktu na podstawie wpisanych danych.
	 * 
	 * @return
	 */
	public ContactId getContactId()
	{
		return new ContactId(getName());
	}

	public void newAvailableContacts(final Contact[] contacts)
	{
		for (Contact c : contacts)
			if (c.getId().equals(getContactId()))
				add.setEnabled(true);
	}

	/**
	 * Ustawia nowego Listenera. Jeżeli wcześniej był ustawiony Listener to
	 * zostanie on usunięty.
	 * 
	 * @param listener Nowy Listener.
	 */
	public void setAddContactWindowListener(
			final AddContactWindowListener listener)
	{
		if (this.listener != null)
			removeAddContactWindowListener();
		this.listener = listener;
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(windowListener);
		add.addActionListener(addListener);
		cancel.addActionListener(cancelListener);
		nameField.getDocument().addDocumentListener(changeListener);
		displayedNameField.getDocument().addDocumentListener(changeListener);
	}

	/**
	 * Zwraca aktualnego Listenera.
	 * 
	 * @return <ul>
	 *         <li>AddContactWindowListener - jeżeli jest przypisany Listener</li>
	 *         <li>null - jeżeli nie ma przypisanego Listenera</li>
	 *         </ul>
	 */
	public AddContactWindowListener getAddContactWindowListener()
	{
		return listener;
	}

	/** Usuwa aktualnego Listenera. */
	public void removeAddContactWindowListener()
	{
		if (listener != null)
		{
			window.removeWindowListener(windowListener);
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			add.removeActionListener(addListener);
			cancel.removeActionListener(cancelListener);
			nameField.getDocument().removeDocumentListener(changeListener);
			displayedNameField.getDocument().removeDocumentListener(
					changeListener);
			listener = null;
		}
	}
}
