package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Kontakt rozpoczął konwersacje.
 */
public class ConversationFromEvent extends ContactEvent
{

	public ConversationFromEvent(ContactId c)
	{
		super(c);
	}

}
