package org.dragons.itsmeim.view;

import javax.swing.text.StyledDocument;

/**
 * Listener akcji okna konwersacji.
 */
public interface ConversationWindowListener
{
	/**
	 * Przesyła informacje związane z wysłaniem nowej wiadomości.
	 * 
	 * @param c Kontakt, który jest adresatem wiadomości
	 * @param message Wiadomość
	 * @param cw Okno konwersacji, z którego pochodzi akcja
	 */
	public void sendMessage(ViewContact c, StyledDocument message,
			ConversationWindow cw);

	/**
	 * Dane okno konwersacji zostało zamknięte.
	 * 
	 * @param cw
	 */
	public void windowClosing(ConversationWindow cw);
}