package pl.michal.debski.itsmeim.view;

/**
 * Obsługa zdarzeń z głównego okna.
 * 
 * @author Michał Dębski
 * @see MainWindow
 */
public interface MainWindowListener
{
	/**
	 * W głównym oknie nastąpiła akcja otworzenia okna konwersacji z kontaktem
	 * <i>c</i>.
	 * 
	 * @param c Kontakt, który jest celem konwersacji.
	 */
	public void openConversationWindow(ViewContact c, MainWindow mw);

	/** Wywołano akcję zamknięcia Głównego Okna. */
	public void mainWindowClosing(MainWindow mw);

	/** Wywołano akcje dodania nowego kontaktu */
	public void openAddContact(MainWindow mw);
}
