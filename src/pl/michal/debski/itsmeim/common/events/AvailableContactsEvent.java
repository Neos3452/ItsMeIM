package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.Contact;

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
