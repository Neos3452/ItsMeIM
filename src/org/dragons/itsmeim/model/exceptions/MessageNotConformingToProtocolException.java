package org.dragons.itsmeim.model.exceptions;

/**
 * Odebrana wiadomość nie jest wiadomością tego protokołu.
 */
public class MessageNotConformingToProtocolException extends ProtocolException
{

	private static final long serialVersionUID = 1L;

	public MessageNotConformingToProtocolException()
	{
	}

	public MessageNotConformingToProtocolException(String arg0)
	{
		super(arg0);
	}

	public MessageNotConformingToProtocolException(Throwable arg0)
	{
		super(arg0);
	}

	public MessageNotConformingToProtocolException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
