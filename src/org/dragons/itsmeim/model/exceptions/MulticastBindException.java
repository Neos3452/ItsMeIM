package org.dragons.itsmeim.model.exceptions;

/**
 * Moduł multicasti nie mógł podłączyć się do socketu.
 */
public class MulticastBindException extends Exception
{

	private static final long serialVersionUID = 1L;

	public MulticastBindException()
	{
	}

	public MulticastBindException(String message)
	{
		super(message);
	}

	public MulticastBindException(Throwable cause)
	{
		super(cause);
	}

	public MulticastBindException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
