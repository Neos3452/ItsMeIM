package org.dragons.itsmeim.controller;

/**
 * W widoku wystąpił błąd, którego nie da się naprawić.
 */
public class FatalViewError extends Exception
{

	private static final long serialVersionUID = 1L;

	public FatalViewError()
	{
	}

	public FatalViewError(String arg0)
	{
		super(arg0);
	}

	public FatalViewError(Throwable arg0)
	{
		super(arg0);
	}

	public FatalViewError(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
