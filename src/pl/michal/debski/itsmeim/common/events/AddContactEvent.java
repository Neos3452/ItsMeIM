package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.Contact;
import pl.michal.debski.itsmeim.common.ContactId;

public class AddContactEvent extends ContactEvent
{
	private final Contact info;

	public AddContactEvent(ContactId id, Contact info)
	{
		super(id);
		this.info = info;
	}

	public Contact getContactInfo()
	{
		return info;
	}
}
