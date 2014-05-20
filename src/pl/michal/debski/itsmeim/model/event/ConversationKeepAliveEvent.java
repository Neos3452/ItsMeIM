package pl.michal.debski.itsmeim.model.event;

import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.ContactEvent;

/**
 * Wiadomość o potrzebie podtrzymania połączenia.
 * 
 * @author Michał Dębski
 */
public class ConversationKeepAliveEvent extends ContactEvent
{

	public ConversationKeepAliveEvent(ContactId c)
	{
		super(c);
	}

}
