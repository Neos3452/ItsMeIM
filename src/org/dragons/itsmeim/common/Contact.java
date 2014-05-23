package org.dragons.itsmeim.common;

/**
 * Reprezentuje kontakt i wszystkie dane o nim.
 */
public class Contact
{
	private final ContactId id;
	private String displayName;

	public Contact(ContactId id)
	{
		this.id = id;
	}

	public Contact(ContactId id, String displayName)
	{
		this.id = id;
		this.displayName = displayName;
	}

	public Contact(Contact other)
	{
		this.id = new ContactId(other.id);
		this.displayName = other.displayName;
	}

	public ContactId getId()
	{
		return id;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
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
