package org.dragons.itsmeim.view;

import org.dragons.itsmeim.common.ContactId;

import static org.dragons.itsmeim.view.ViewContactState.*;

/**
 * Opisuje kontakt w widoku.
 */
public class ViewContact
{
	private ViewContactState state = OFF;
	private final ContactId id;
	private String displayName;

	/**
	 * Tworzy nowy kontakt.
	 * 
	 * @param id Numer kontaktu.
	 * @param state Stan dostępności.
	 * @param displayName Nazwa do wyświetlania.
	 */
	public ViewContact(final ContactId id, final ViewContactState state,
			final String displayName)
	{
		this.id = id;
		this.state = state;
		this.displayName = displayName;
	}

	/**
	 * Tworzy nowy kontakt, z wyświetlaną nazwą taką jak id.
	 * 
	 * @param id Numer kontaktu.
	 * @param state Stan dostępności.
	 */
	public ViewContact(final ContactId id, final ViewContactState state)
	{
		this(id, state, id.getName());
	}

	/**
	 * Zmienia stan kontaktu.
	 * 
	 * @param state
	 */
	public void setState(final ViewContactState state)
	{
		this.state = state;
	}

	/**
	 * Zwraca stan kontaktu.
	 * 
	 * @return
	 */
	public ViewContactState getState()
	{
		return state;
	}

	/**
	 * Zwraca Id kontaktu.
	 * 
	 * @return
	 */
	public ContactId getContactId()
	{
		return id;
	}

	/**
	 * Zwraca nazwę wyświetlaną kontaktu.
	 * 
	 * @return
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Ustaiwa nową wyświetlaną nazwę kontaktu.
	 * 
	 * @param name
	 */
	public void setDisplayName(final String name)
	{
		displayName = name;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewContact other = (ViewContact) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
}
