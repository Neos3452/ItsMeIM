package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.Contact;

public class AvailableContactsEvent extends Event
{
	private final Contact[] contacts;

	public AvailableContactsEvent(Contact[] contacts)
	{
		this.contacts = contacts;
	}

	public Contact[] getContacts()
	{
		return contacts;
	}
}
