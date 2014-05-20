package pl.michal.debski.itsmeim.common;

/**
 * Reprezentuje id kontaktu jednoznacznie identyfikujące go.
 * 
 * @author Michał Dębski
 */
public class ContactId
{
	final private String name;

	public ContactId(String name)
	{
		this.name = name;
	}

	public ContactId(ContactId other)
	{
		this.name = other.name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "[ContactId=\"" + name + "\"]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ContactId other = (ContactId) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
}
