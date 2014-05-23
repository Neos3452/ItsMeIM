package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Użytkownik zalogował się.
 */
public class LogedInEvent extends ContactEvent
{
	private String picked;

	public LogedInEvent(ContactId c)
	{
		super(c);
	}

	public LogedInEvent(ContactId c, String picked)
	{
		super(c);
		this.picked = picked;
	}

	public String getPicked()
	{
		return picked;
	}
}
