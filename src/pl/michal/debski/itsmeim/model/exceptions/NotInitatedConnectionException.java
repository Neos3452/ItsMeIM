package pl.michal.debski.itsmeim.model.exceptions;

/**
 * Nie wolno odbierać, ani wysyłać wiadomości przed zainicjowaniem połącznienia.
 * 
 * @author Michał Dębski
 */
public class NotInitatedConnectionException extends ConnectionException
{

	private static final long serialVersionUID = 1L;

	public NotInitatedConnectionException()
	{
	}

	public NotInitatedConnectionException(String arg0)
	{
		super(arg0);
	}

	public NotInitatedConnectionException(Throwable arg0)
	{
		super(arg0);
	}

	public NotInitatedConnectionException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
