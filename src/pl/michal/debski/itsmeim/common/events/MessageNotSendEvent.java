package pl.michal.debski.itsmeim.common.events;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Nie udało się wysłać wiadomości do kontaktu.
 * 
 * @author Michał Dębski
 */
public class MessageNotSendEvent extends ContactEvent
{
	MessageToEvent message;

	public MessageNotSendEvent(ContactId c, MessageToEvent message)
	{
		super(c);
		this.message = message;
	}

	public MessageToEvent getMessage()
	{
		return message;
	}
}
