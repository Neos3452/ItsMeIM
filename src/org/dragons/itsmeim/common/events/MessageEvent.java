package org.dragons.itsmeim.common.events;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

import org.dragons.itsmeim.common.ContactId;

/**
 * Zdarzenie przesłania informacji wygenerowanej przez użytkownika.
 */
public abstract class MessageEvent extends ContactEvent
{
	private final DefaultStyledDocument message;
	private final Date time;

	public MessageEvent(ContactId c, Date time, DefaultStyledDocument message)
	{
		super(c);
		this.time = time;
		this.message = message;
	}

	public Date getTime()
	{
		return time;
	}

	public DefaultStyledDocument getMessage()
	{
		return message;
	}
}
