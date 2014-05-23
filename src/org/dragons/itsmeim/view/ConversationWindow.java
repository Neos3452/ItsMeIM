package org.dragons.itsmeim.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Okno do rozmowy.
 *         <p>
 *         <b>Uwaga:</b> Obiekty widoku nie są "Thread safe".
 *         </p>
 */
class ConversationWindow
{

	final private ViewContact contact;
	final private JFrame window;

	private ConversationWindowListener listener = null;

	private final StyledDocument conversationText = new DefaultStyledDocument();
	private final JTextPane conversationTextPane = new JTextPane(
			conversationText);
	private final JScrollPane conversationScrollPanel = new JScrollPane(
			conversationTextPane);

	private final JPanel inputPanel = new JPanel(new BorderLayout());
	private final JPanel inputPanelStylePanel = new JPanel(new FlowLayout(
			FlowLayout.LEFT, 0, 0));
	private final JToggleButton inputPanelStylePanelBoldButton = new JToggleButton(
			"<html><b>B");
	private final JToggleButton inputPanelStylePanelItalicButton = new JToggleButton(
			"<html><i>I");
	private final JToggleButton inputPanelStylePanelUnderlineButton = new JToggleButton(
			"<html><u>U");
	private final JTextPane inputPanelTextPane = new JTextPane();
	private final MutableAttributeSet inputPanelTextPaneInputAtrributeSet = inputPanelTextPane
			.getInputAttributes();
	private final JPanel inputPanelSendPanel = new JPanel(new FlowLayout(
			FlowLayout.RIGHT, 5, 0));
	private final JScrollPane inputPanelScrollPane = new JScrollPane(
			inputPanelTextPane);
	private final JButton inputPanelSendPanelSendButton = new JButton("Send");
	private final JCheckBox inputPanelSendPanelSendCheck = new JCheckBox(
			"Enter sends the message", true);

	private final JSplitPane splitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, conversationScrollPanel, inputPanel);

	/**
	 * Słucha na wciśnięcie ENTER do wysłania wiadomości.
	 */
	private final KeyListener inputListener = new KeyAdapter()
	{
		@Override
		public void keyPressed(KeyEvent event)
		{
			// shift + enter = new line
			if (event.getKeyCode() == KeyEvent.VK_ENTER
					&& event.getModifiers() == KeyEvent.SHIFT_MASK
					&& inputPanelSendPanelSendCheck.isSelected())
			{
				try
				{
					inputPanelTextPane.getStyledDocument().insertString(
							inputPanelTextPane.getStyledDocument().getLength(),
							"\n", inputPanelTextPaneInputAtrributeSet);
				}
				catch (BadLocationException e1)
				{
					// To nie powinno się zdarzyć, w każdym wypadku zignorować
				}
				event.consume();
				return;
			}
			if (event.getKeyCode() == KeyEvent.VK_ENTER
					&& inputPanelSendPanelSendCheck.isSelected())
			{
				send();
				event.consume();
			}
		}
	};
	private final WindowListener windowListener = new WindowAdapter()
	{
		@Override
		public void windowClosing(final WindowEvent event)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.windowClosing(ConversationWindow.this);
				}
			});
		}
	};

	/**
	 * Inicjalizuje okno. Powinno być wywoływane tylko z konstruktora.
	 */
	private void init()
	{
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setMinimumSize(new Dimension(inputPanelSendPanelSendButton
				.getPreferredSize().width
				+ inputPanelSendPanelSendCheck.getPreferredSize().width + 20,
				140));
		window.setPreferredSize(new Dimension(500, 500));
		conversationScrollPanel.getVerticalScrollBar().setUnitIncrement(2);

		inputPanel.add(inputPanelScrollPane, BorderLayout.CENTER);
		inputPanelStylePanel.setPreferredSize(new Dimension(window
				.getPreferredSize().width, 30));
		inputPanelStylePanel.add(inputPanelStylePanelBoldButton);
		inputPanelStylePanelBoldButton.setFocusable(false);
		inputPanelStylePanelBoldButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				if (((JToggleButton) e.getSource()).isSelected())
				{
					StyleConstants.setBold(inputPanelTextPaneInputAtrributeSet,
							true);
				}
				else
				{
					StyleConstants.setBold(inputPanelTextPaneInputAtrributeSet,
							false);
				}
			}
		});
		inputPanelStylePanel.add(inputPanelStylePanelItalicButton);
		inputPanelStylePanelItalicButton.setFocusable(false);
		inputPanelStylePanelItalicButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				if (((JToggleButton) e.getSource()).isSelected())
				{
					StyleConstants.setItalic(
							inputPanelTextPaneInputAtrributeSet, true);
				}
				else
				{
					StyleConstants.setItalic(
							inputPanelTextPaneInputAtrributeSet, false);
				}
			}
		});
		inputPanelStylePanel.add(inputPanelStylePanelUnderlineButton);
		inputPanelStylePanelUnderlineButton.setFocusable(false);
		inputPanelStylePanelUnderlineButton
				.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(final ActionEvent e)
					{
						if (((JToggleButton) e.getSource()).isSelected())
						{
							StyleConstants.setUnderline(
									inputPanelTextPaneInputAtrributeSet, true);
						}
						else
						{
							StyleConstants.setUnderline(
									inputPanelTextPaneInputAtrributeSet, false);
						}
					}
				});
		inputPanel.add(inputPanelStylePanel, BorderLayout.NORTH);
		inputPanelSendPanel.setPreferredSize(new Dimension(window
				.getPreferredSize().width, inputPanelSendPanelSendButton
				.getPreferredSize().height));
		inputPanelSendPanelSendCheck
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		inputPanelSendPanel.add(inputPanelSendPanelSendCheck);
		inputPanelSendPanelSendButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent arg0)
			{
				send();
			}
		});
		inputPanelSendPanel.add(inputPanelSendPanelSendButton);
		inputPanel.add(inputPanelSendPanel, BorderLayout.SOUTH);
		inputPanelTextPane.addKeyListener(inputListener);
		inputPanelTextPane.setBackground(Color.LIGHT_GRAY);

		conversationTextPane.setEditable(false);

		splitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		splitPane.setDividerLocation(350);
		splitPane.setDividerSize(5);
		splitPane.setResizeWeight(1);
		window.add(splitPane);
		window.pack();
	}

	/**
	 * Tworzy nowe okno konwersacji z danym kontaktem i ustawia tytuł okna na
	 * displayName kontaktu.
	 * 
	 * @param contact Kontakt, z którym ma być prowadzona rozmowa.
	 * @throws HeadlessException
	 */
	public ConversationWindow(final ViewContact contact)
			throws HeadlessException
	{
		window = new JFrame(contact.getDisplayName());
		this.contact = contact;
		init();
	}

	/**
	 * Tworzy nowe okno konwersacji z danym kontaktem i ustawia tytuł okna na
	 * podany.
	 * 
	 * @param contact Kontakt, z którym ma być prowadzona rozmowa.
	 * @param title Tytuł okna.
	 * @throws HeadlessException
	 */
	public ConversationWindow(final ViewContact contact, final String title)
			throws HeadlessException
	{
		window = new JFrame(title);
		this.contact = contact;
		init();
	}

	/**
	 * Ustawia nowego Listenera. Jeżeli wcześniej był ustawiony Listener to
	 * zostanie on usunięty.
	 * 
	 * @param listener Nowy Listener.
	 */
	public void setConversationWindowListener(
			final ConversationWindowListener listener)
	{
		if (this.listener != null)
			removeConversationWindowListener();
		this.listener = listener;
		window.addWindowListener(windowListener);
	}

	/**
	 * Zwraca aktualnego Listenera.
	 * 
	 * @return <ul>
	 *         <li>ConversationWindowListener - jeżeli jest przypisany Listener</li>
	 *         <li>null - jeżeli nie ma przypisanego Listenera</li>
	 *         </ul>
	 */
	public ConversationWindowListener getConversationWindowListener()
	{
		return listener;
	}

	/**
	 * Usuwa aktualnego Listenera.
	 */
	public void removeConversationWindowListener()
	{
		if (listener != null)
		{
			window.removeWindowListener(windowListener);
			listener = null;
		}
	}

	/**
	 * Ustawia preferowany rozmiar okna.
	 * 
	 * @param dimension
	 */
	public void setPreferredSize(final Dimension dimension)
	{
		window.setPreferredSize(dimension);
	}

	/**
	 * Ustawia lewy górny róg okna w podanym punkcie.
	 * 
	 * @param point
	 */
	public void setLocation(final Point point)
	{
		window.setLocation(point);
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
	 * Mówi czy okno jest widoczne
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return window.isVisible();
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

	/** Usuwa okno rozmowy. */
	public void dispose()
	{
		window.dispose();
	}

	/**
	 * Zwraca kontakt, z którym prowadzona jest konwersacja.
	 * 
	 * @return
	 */
	public ViewContact getContact()
	{
		return contact;
	}

	/**
	 * Wypisuje wiadomość na ekran konwersacji.
	 * 
	 * @param message Otrzymana wiadomość, zawartość jest kopiowana na ekran.
	 * @param time Znacznik czasu wiadomości.
	 */
	public void messageReceived(final StyledDocument message, final Date time)
	{
		final String divder = "#---\n" + "On "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time)
				+ " " + contact.getDisplayName() + " wrote:\n";
		try
		{
			conversationText.insertString(conversationText.getLength(), divder,
					null);
			for (int i = 0; i < message.getLength(); ++i)
				conversationText.insertString(conversationText.getLength(),
						message.getText(i, 1), message.getCharacterElement(i)
								.getAttributes());
			conversationText.insertString(conversationText.getLength(), "\n",
					null);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		conversationTextPane.repaint();
	}

	/**
	 * Wiadomość nie została wysłana.
	 * 
	 * @param message
	 * @param time
	 */
	public void messageNotSend(final StyledDocument message, final Date time)
	{
		try
		{
			conversationText.insertString(conversationText.getLength(),
					"--Message not send--\n", null);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		conversationTextPane.repaint();
	}

	/**
	 * Wyświetla wpisaną wiadomość na ekran. Dodaje tytuł wiadomości(w tym
	 * czas). Wysyła informacje do listenera oraz czyści pole do wpisywania.
	 */
	private void send()
	{
		if (inputPanelTextPane.getDocument().getLength() == 0)
			return;
		final String divder = "[]---\n"
				+ "On "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
						.getInstance().getTime()) + " you wrote:\n";
		final StyledDocument inputText = inputPanelTextPane.getStyledDocument();
		try
		{
			conversationText.insertString(conversationText.getLength(), divder,
					null);
			for (int i = 0; i < inputText.getLength(); ++i)
				conversationText.insertString(conversationText.getLength(),
						inputText.getText(i, 1),
						inputText.getCharacterElement(i).getAttributes());
			conversationText.insertString(conversationText.getLength(), "\n",
					null);
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException("Error reading message!", e);
		}
		if (listener != null)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					listener.sendMessage(contact, inputText,
							ConversationWindow.this);

				}
			});
		}
		inputPanelTextPane.setStyledDocument(new DefaultStyledDocument());
		conversationTextPane.repaint();
	}
}
