package pl.michal.debski.itsmeim.model.exceptions;

/**
 * Oznacza, że połączenie zostało już zainicjowane i nie wolno robić tego
 * ponownie, bez zamknięcia poprzedniego.
 * 
 * @author Michał Dębski
 */
public class AlreadyInitatedConnectionException extends ConnectionException
{

	private static final long serialVersionUID = 1L;

	public AlreadyInitatedConnectionException()
	{
		super();
	}

	public AlreadyInitatedConnectionException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public AlreadyInitatedConnectionException(String arg0)
	{
		super(arg0);
	}

	public AlreadyInitatedConnectionException(Throwable arg0)
	{
		super(arg0);
	}

}
