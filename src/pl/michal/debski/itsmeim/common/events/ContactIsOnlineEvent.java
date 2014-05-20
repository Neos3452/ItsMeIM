package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Kontakt jest dostępny.
 * 
 * @author Michał Dębski
 */
public class ContactIsOnlineEvent extends ContactEvent
{

	public ContactIsOnlineEvent(ContactId c)
	{
		super(c);
	}
}
