package org.dragons.itsmeim.view.exceptions;

/**
 * Wyjątek modułu view.
 */
public class ViewException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ViewException()
	{
	}

	public ViewException(String arg0)
	{
		super(arg0);
	}

	public ViewException(Throwable arg0)
	{
		super(arg0);
	}

	public ViewException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
