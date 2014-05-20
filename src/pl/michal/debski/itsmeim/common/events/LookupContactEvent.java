package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.Contact;

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
