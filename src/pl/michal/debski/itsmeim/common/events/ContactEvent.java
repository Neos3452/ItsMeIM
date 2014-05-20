package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Zdarzenie informacji dotyczące kontaktu.
 * 
 * @author Michał Dębski
 */
public abstract class ContactEvent extends Event
{
	private final ContactId contact;

	public ContactEvent(ContactId id)
	{
		contact = id;
	}

	public ContactId getContactId()
	{
		return contact;
	}
}
