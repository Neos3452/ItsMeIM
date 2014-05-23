package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

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
