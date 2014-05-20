package pl.michal.debski.itsmeim.model.event;

import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.ContactEvent;

/**
 * Wiadomość po potrzebie poinformowania kontaktu o tym, że użytkownik jest
 * dostępny(Informacje o połączniu są zawarte w wiadomości identyfikacyjnej).
 * 
 * @author Michał Dębski
 */
public class ImOnlineEvent extends ContactEvent
{

	public ImOnlineEvent(ContactId id)
	{
		super(id);
	}

}
