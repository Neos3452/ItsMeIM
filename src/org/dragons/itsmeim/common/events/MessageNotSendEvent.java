package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Nie udało się wysłać wiadomości do kontaktu.
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
