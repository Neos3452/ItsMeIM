package org.dragons.itsmeim.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Okno do logowania. Wyświetla pola do zalogowania się użytkowanika,
 * ewentualnie przekazną listę elementów do wyboru.
 *         <p>
 *         <b>Uwaga:</b> Obiekty widoku nie są "Thread safe".
 *         </p>
 */
public class LoginWindow
{
	private LoginWindowListener listener = null;
	private final JFrame window = new JFrame("It's me!");
	private final JPanel namePanel = new JPanel(new BorderLayout());
	private final JLabel nameLabel = new JLabel("Enter Your name:");
	private final JTextField nameField = new JTextField();
	private JPanel interfacePanel;
	private JLabel interfaceLabel;
	private JComboBox<String> interfaceList;
	private final JPanel buttonPanel = new JPanel(new FlowLayout());
	private final JButton loginButton = new JButton("Login");
	private final JButton exitButton = new JButton("Exit");
	{
		try
		{
			final BufferedImage logo = ImageIO.read(this.getClass()
					.getResource("logo.png"));
			final JLabel image = new JLabel(new ImageIcon(logo));
			image.setAlignmentX(Component.CENTER_ALIGNMENT);
			image.setMinimumSize(new Dimension(logo.getWidth(), logo
					.getHeight()));
			window.add(image);
		}
		catch (IOException e)
		{
			/*
			 * Błąd odczytu obrazka, zostanie wyświetlone okno bez niego.
			 */
			e.printStackTrace();
		}
		window.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final WindowEvent e)
			{
				exit();
			}
		});
		/**
		 * Słucha na wprowadzone zmiany, bo nazwa użytkownika nie może być
		 * pusta.
		 */
		nameField.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void removeUpdate(final DocumentEvent arg0)
			{
				if (nameField.getDocument().getLength() == 0)
				{
					loginButton.setEnabled(false);
				}
			}

			@Override
			public void insertUpdate(final DocumentEvent arg0)
			{
				if (nameField.getDocument().getLength() > 0)
				{
					loginButton.setEnabled(true);
				}
			}

			@Override
			public void changedUpdate(final DocumentEvent arg0)
			{
				if (nameField.getDocument().getLength() == 0)
				{
					loginButton.setEnabled(false);
				}
				else if (nameField.getDocument().getLength() > 0)
				{
					loginButton.setEnabled(true);
				}
			}
		});
		nameField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(final KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						&& loginButton.isEnabled())
				{
					login();
				}
			}
		});
		namePanel.add(nameField, BorderLayout.NORTH);

		final JPanel nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		window.add(nameLabelPanel);

		window.add(namePanel);

		loginButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				login();
			}
		});
		loginButton.setEnabled(false);
		buttonPanel.add(loginButton);
		exitButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				exit();
			}
		});
		buttonPanel.add(exitButton);
	}

	/**
	 * Konstruuje okno do logowania z lista, z której można wybrać jeden z
	 * przekazanych elementów.
	 * 
	 * @param interfaces Tablica elementów do wyboru, po jednym na wpis.
	 */
	public LoginWindow(final Collection<String> interfaces)
	{
		interfacePanel = new JPanel(new BorderLayout());
		interfaceLabel = new JLabel("Pick Network Interface:");
		final String[] interfacesArray = new String[interfaces.size()];
		interfaces.toArray(interfacesArray);
		interfaceList = new JComboBox<String>(interfacesArray);
		final JPanel interfaceLabelPanel = new JPanel();
		interfaceLabelPanel.add(interfaceLabel);
		window.add(interfaceLabelPanel);
		interfacePanel.add(interfaceList, BorderLayout.NORTH);
		window.add(interfacePanel);
		window.add(buttonPanel);
		window.setLayout(new BoxLayout(window.getContentPane(),
				BoxLayout.Y_AXIS));
		window.pack();
		window.setMinimumSize(window.getSize());
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Domyślne okno do logowania, tylko do zalogowania.
	 */
	public LoginWindow()
	{
		window.add(buttonPanel);
		window.setLayout(new BoxLayout(window.getContentPane(),
				BoxLayout.Y_AXIS));
		window.pack();
		window.setMinimumSize(window.getSize());
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Zmienia widoczność okna.
	 * 
	 * @param visibility
	 */
	public void setVisible(final boolean visibility)
	{
		window.setVisible(visibility);
	}

	/**
	 * Zwraca wpisany tekst w polu "name".
	 * 
	 * @return
	 */
	public String getName()
	{
		return nameField.getText();
	}

	/**
	 * Zwraca wybrany element.
	 * 
	 * @return
	 */
	public String getInterface()
	{
		if (interfaceList == null)
			return null;
		return (String) interfaceList.getSelectedItem();
	}

	/**
	 * Zwraca numer wybranego elementu.
	 * 
	 * @return
	 */
	public int getPicked()
	{
		if (interfaceList == null)
			return -1;
		return interfaceList.getSelectedIndex();
	}

	/**
	 * Wyświetla użytkownikowi, że logowanie się nie powiodło.
	 */
	public void loginIncorrect()
	{
		nameLabel.setForeground(Color.RED);
		nameLabel.setText("Login incorrect, enter Your name:");
	}

	/**
	 * Wyświetla użytkownikowi, że logowanie powiodło się.
	 */
	public void loginSuccessful()
	{
		// nothing to do here
	}

	/**
	 * Ustawia Listenera okna logowania.
	 * 
	 * @param loginWindowListener
	 */
	public void setLoginWindowListener(final LoginWindowListener loginWindowListener)
	{
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		listener = loginWindowListener;
	}

	/**
	 * Usuwa listenera okna logowania.
	 */
	public void removeLoginWindowListener()
	{
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		listener = null;
	}

	/**
	 * Zwraca Listenera okna logowania.
	 * 
	 * @return
	 */
	public LoginWindowListener getLoginWindowListener()
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
	 * Wysyła informacje o logowaniu do listenera.
	 */
	private void login()
	{
		if (listener != null)
			listener.logIn(this);
	}

	/**
	 * Wysyła informacje o wyjściu do listenera.
	 */
	private void exit()
	{
		if (listener != null)
			listener.exit(this);
	}
}
