package org.dragons.itsmeim.common.events;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

import org.dragons.itsmeim.common.ContactId;

/**
 * Otrzymano wiadomość.
 */
public class MessageFromEvent extends MessageEvent
{
	public MessageFromEvent(ContactId c, Date time,
			DefaultStyledDocument message)
	{
		super(c, time, message);
	}
}
