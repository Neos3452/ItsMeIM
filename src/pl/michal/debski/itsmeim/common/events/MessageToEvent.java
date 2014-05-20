package pl.michal.debski.itsmeim.common.events;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

import pl.michal.debski.itsmeim.common.ContactId;

/**
 * Wiadomość do kontaktu.
 * 
 * @author Michał Dębski
 */
public class MessageToEvent extends MessageEvent
{
	public MessageToEvent(ContactId c, Date time, DefaultStyledDocument message)
	{
		super(c, time, message);
	}
}
