package pl.michal.debski.itsmeim.model.exceptions;

/**
 * Wyjątek protokołu.
 * 
 * @author Michał Dębski
 */
public abstract class ProtocolException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ProtocolException()
	{
	}

	public ProtocolException(String arg0)
	{
		super(arg0);
	}

	public ProtocolException(Throwable arg0)
	{
		super(arg0);
	}

	public ProtocolException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
