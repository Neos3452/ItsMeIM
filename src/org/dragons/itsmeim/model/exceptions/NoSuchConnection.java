package org.dragons.itsmeim.model.exceptions;

/**
 * Nie ma takiego połącznia.
 */
public class NoSuchConnection extends Exception
{

	private static final long serialVersionUID = 1L;

	public NoSuchConnection()
	{
		super();
	}

	public NoSuchConnection(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public NoSuchConnection(String arg0)
	{
		super(arg0);
	}

	public NoSuchConnection(Throwable arg0)
	{
		super(arg0);
	}

}
