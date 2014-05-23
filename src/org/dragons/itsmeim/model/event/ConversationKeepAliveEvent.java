package org.dragons.itsmeim.model.event;

import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.ContactEvent;

/**
 * Wiadomość o potrzebie podtrzymania połączenia.
 */
public class ConversationKeepAliveEvent extends ContactEvent
{

	public ConversationKeepAliveEvent(ContactId c)
	{
		super(c);
	}

}
