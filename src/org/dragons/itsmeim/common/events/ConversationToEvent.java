package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Użytkownik rozpoczął konwersjacje z kontaktem.
 */
public class ConversationToEvent extends ContactEvent
{

	public ConversationToEvent(ContactId c)
	{
		super(c);
	}

}
