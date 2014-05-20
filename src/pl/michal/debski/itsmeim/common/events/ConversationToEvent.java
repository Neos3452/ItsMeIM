package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Użytkownik rozpoczął konwersjacje z kontaktem.
 * 
 * @author Michał Dębski
 */
public class ConversationToEvent extends ContactEvent
{

	public ConversationToEvent(ContactId c)
	{
		super(c);
	}

}
