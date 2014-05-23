package org.dragons.itsmeim.model;

import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.ContactEvent;
import org.dragons.itsmeim.model.event.ConnectionInformationEvent;
import org.dragons.itsmeim.model.exceptions.MessageNotConformingToProtocolException;

/**
 * Interface opisujący protokuł.
 */
public interface Protocol
{
	/**
	 * Tłumaczy wiadomość na odpowiadające jej zdarzenie.
	 * 
	 * @param o Wiadomość do przetłumaczenia.
	 * @param c Nadawca wiadomości(pole opcjonalne, może być null).
	 * @return Zdarzenie, które odpowiada wiadomości w danym protokole.
	 * @throws MessageNotConformingToProtocolException Jeżeli wiadomość nie
	 *             przystaje do protokołu.
	 */
	public ContactEvent processInput(Object o, ContactId c)
			throws MessageNotConformingToProtocolException;

	/**
	 * Tłumaczy zdarzenie na wiadomość gotową do przesłania.
	 * 
	 * @param e Zdarzenie do przetłumaczenia.
	 * @param c Kontakt, który wysyła wiadomość(pole opcjonalne, może być null).
	 * @return Wiadomość gotowa do wysłania.
	 * @throws MessageNotConformingToProtocolException Jeżeli wiadomość nie
	 *             przystaje do protokołu.
	 */
	public Object generateOutput(ContactEvent e, ContactId c);

	/**
	 * Tłumaczy pierwszą wiadomość na zdarzenie określające kto zainicjował
	 * połączenie.
	 * 
	 * @param o Wiadomość do przetłumaczenia.
	 * @return Zdarzenie określające nadawce.
	 * @throws MessageNotConformingToProtocolException Jeżeli wiadomość nie
	 *             przystaje do protokołu lub nie jest to pierwsza wiadomość.
	 */
	public ConnectionInformationEvent processFirstMessage(Object o)
			throws MessageNotConformingToProtocolException;

	/**
	 * Generuje pierwszą wiadomość.
	 * 
	 * @param e Zdarzenie określające nadawce.
	 * @return Wiadomość gotową do wysłania.
	 */
	public Object generateFirstMessage(ConnectionInformationEvent e);
}
