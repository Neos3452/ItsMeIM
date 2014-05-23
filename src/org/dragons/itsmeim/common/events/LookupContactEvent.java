package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.Contact;

public class LookupContactEvent extends Event
{
	private final Contact info;

	public LookupContactEvent(Contact info)
	{
		this.info = info;
	}

	public Contact getContact()
	{
		return info;
	}
}
