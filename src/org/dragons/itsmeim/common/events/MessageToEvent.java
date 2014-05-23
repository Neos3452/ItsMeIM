package org.dragons.itsmeim.common.events;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

import org.dragons.itsmeim.common.ContactId;

/**
 * Wiadomość do kontaktu.
 */
public class MessageToEvent extends MessageEvent
{
	public MessageToEvent(ContactId c, Date time, DefaultStyledDocument message)
	{
		super(c, time, message);
	}
}
