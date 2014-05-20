package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Zakończenie konwersacji.
 * 
 * @author Michał Dębski
 */
public class ConversationEndedEvent extends ContactEvent
{

	public ConversationEndedEvent(ContactId c)
	{
		super(c);
	}

}
