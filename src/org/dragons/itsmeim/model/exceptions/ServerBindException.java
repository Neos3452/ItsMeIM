package org.dragons.itsmeim.model.exceptions;

/**
 * Moduł odbierający połącznia nie mógł podłączyć się do socketu.
 */
public class ServerBindException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ServerBindException()
	{
		super();
	}

	public ServerBindException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ServerBindException(String message)
	{
		super(message);
	}

	public ServerBindException(Throwable cause)
	{
		super(cause);
	}

}
