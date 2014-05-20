package pl.michal.debski.itsmeim.common.events;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Otrzymano wiadomość.
 * 
 * @author Michał Dębski
 */
public class MessageFromEvent extends MessageEvent
{
	public MessageFromEvent(ContactId c, Date time,
			DefaultStyledDocument message)
	{
		super(c, time, message);
	}
}
