package org.dragons.itsmeim.model.exceptions;

/**
 * Sytuacja wyjątkowa w aktywnym połączeniu(Oznacz błąd programistyczny!).
 */
public class ConnectionException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public ConnectionException()
	{
	}

	public ConnectionException(String arg0)
	{
		super(arg0);
	}

	public ConnectionException(Throwable arg0)
	{
		super(arg0);
	}

	public ConnectionException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
