package org.dragons.itsmeim.model.event;

import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.ContactEvent;

/**
 * Wiadomość po potrzebie poinformowania kontaktu o tym, że użytkownik jest
 * dostępny(Informacje o połączniu są zawarte w wiadomości identyfikacyjnej).
 */
public class ImOnlineEvent extends ContactEvent
{

	public ImOnlineEvent(ContactId id)
	{
		super(id);
	}

}
