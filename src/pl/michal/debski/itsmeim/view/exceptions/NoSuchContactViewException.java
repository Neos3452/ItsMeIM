package pl.michal.debski.itsmeim.view.exceptions;

/**
 * Nie ma takiego kontaktu.
 * 
 * @author Michał Dębski
 */
public class NoSuchContactViewException extends ViewException
{
	private static final long serialVersionUID = 1L;

	public NoSuchContactViewException()
	{
	}

	public NoSuchContactViewException(String arg0)
	{
		super(arg0);
	}

	public NoSuchContactViewException(Throwable arg0)
	{
		super(arg0);
	}

	public NoSuchContactViewException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
