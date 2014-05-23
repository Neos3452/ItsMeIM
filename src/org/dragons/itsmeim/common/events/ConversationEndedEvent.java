package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Zakończenie konwersacji.
 */
public class ConversationEndedEvent extends ContactEvent
{

	public ConversationEndedEvent(ContactId c)
	{
		super(c);
	}

}
