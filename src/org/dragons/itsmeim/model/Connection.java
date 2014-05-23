package org.dragons.itsmeim.model;

import java.net.InetAddress;

import org.dragons.itsmeim.common.ContactId;

/**
 * Reprezentuje adres, na którym kontakt odbiera połączenia.
 */
public class Connection
{
	final private ContactId contact;
	private InetAddress address;
	private int port;
	private ActiveConnection currentConnection = null;
	private int state = 0;
	/** Do sprawdzania dostępności. */
	private ScheduleTask onlineCheck;

	public Connection(final ContactId id, final InetAddress address,
			final int port, final ScheduleTask onlineCheck)
	{
		this.contact = id;
		this.address = address;
		this.port = port;
		this.onlineCheck = onlineCheck;
	}

	public ContactId getContactId()
	{
		return contact;
	}

	public void setAddress(final InetAddress address)
	{
		this.address = address;
	}

	public InetAddress getAddress()
	{
		return address;
	}

	public void setPort(final int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}

	public ActiveConnection getCurrentConnection()
	{
		return currentConnection;
	}

	public void setCurrentConnection(final ActiveConnection currentConnection)
	{
		this.currentConnection = currentConnection;
	}

	public int getState()
	{
		return state;
	}

	public void setState(final int state)
	{
		this.state = state;
	}

	public ScheduleTask getOnlineCheck()
	{
		return onlineCheck;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + port;
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
		Connection other = (Connection) obj;
		if (address == null)
		{
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		if (contact == null)
		{
			if (other.contact != null)
				return false;
		}
		else if (!contact.equals(other.contact))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
}
