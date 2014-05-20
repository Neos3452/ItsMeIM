package pl.michal.debski.itsmeim.model.conversationprotocol.words;

import java.util.Date;

import javax.swing.text.DefaultStyledDocument;

/**
 * Zawiera wiadomość, która została wysłana przez użytkownika.
 * 
 * @author Michał Dębski
 */
public class MessageConversationProtocolWord extends ConversationProtocolWord
{
	private static final long serialVersionUID = 1L;
	private final DefaultStyledDocument message;
	private final Date time;

	public MessageConversationProtocolWord(Date time,
			DefaultStyledDocument message)
	{
		this.message = message;
		this.time = time;
	}

	public DefaultStyledDocument getMessage()
	{
		return message;
	}

	public Date getTime()
	{
		return time;
	}
}
