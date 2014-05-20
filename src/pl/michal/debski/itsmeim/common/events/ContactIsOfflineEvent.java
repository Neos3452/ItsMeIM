package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Kontakt jest niedostępny.
 * 
 * @author Michał Dębski
 */
public class ContactIsOfflineEvent extends ContactEvent
{

	public ContactIsOfflineEvent(ContactId c)
	{
		super(c);
	}
}
