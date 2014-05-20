package pl.michal.debski.itsmeim.model.event;

import java.net.InetAddress;

import pl.michal.debski.itsmeim.common.ContactId;
import pl.michal.debski.itsmeim.common.events.ContactEvent;

/**
 * Wiadomość przekazująca informacje o połączeniu z danym kontaktem.
 * 
 * @author Michał Dębski
 */
public class ConnectionInformationEvent extends ContactEvent
{
	private InetAddress address;
	private int port;

	public ConnectionInformationEvent(ContactId contact, InetAddress address,
			int port)
	{
		super(contact);
		this.address = address;
		this.port = port;
	}

	public InetAddress getAddress()
	{
		return address;
	}

	public int getPort()
	{
		return port;
	}
}
