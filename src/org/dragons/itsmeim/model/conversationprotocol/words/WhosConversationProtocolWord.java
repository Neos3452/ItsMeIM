package org.dragons.itsmeim.model.conversationprotocol.words;

import java.net.InetAddress;

/**
 * Wiadomośc służąca do identyfikacji, przekazuje też informacje, gdzie ten
 * użytkownik odbiera połączenia.
 */
public class WhosConversationProtocolWord extends ConversationProtocolWord
{

	private static final long serialVersionUID = 1L;
	private String name;
	private InetAddress address;
	private int port;

	public WhosConversationProtocolWord(String name, InetAddress address,
			int port)
	{
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
