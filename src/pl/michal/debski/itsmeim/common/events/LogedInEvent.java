package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Użytkownik zalogował się.
 * 
 * @author Michał Dębski
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
