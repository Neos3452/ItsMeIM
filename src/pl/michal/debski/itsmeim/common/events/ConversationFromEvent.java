package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Kontakt rozpoczął konwersacje.
 * 
 * @author Michał Dębski
 */
public class ConversationFromEvent extends ContactEvent
{

	public ConversationFromEvent(ContactId c)
	{
		super(c);
	}

}
