package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.Contact;
import org.dragons.itsmeim.common.ContactId;

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
