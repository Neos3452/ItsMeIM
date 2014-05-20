package pl.michal.debski.itsmeim.model.multicastprotocol;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Informacje o możliwym połączeniu transmitowane przez multicast.
 * 
 * @author Michał Dębski
 */
public class ContactInformation implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private InetAddress address;
	private int port;

	public ContactInformation(String name, InetAddress address, int port)
	{
		super();
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getName()
	{
		return name;
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
