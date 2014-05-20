package pl.michal.debski.itsmeim.controller;

/**
 * W modelu wystąpił błąd, którego nie da się naprawić.
 * 
 * @author Michał Dębski
 */
public class FatalModelError extends Exception
{

	private static final long serialVersionUID = 1L;

	public FatalModelError()
	{
	}

	public FatalModelError(String arg0)
	{
		super(arg0);
	}

	public FatalModelError(Throwable arg0)
	{
		super(arg0);
	}

	public FatalModelError(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
