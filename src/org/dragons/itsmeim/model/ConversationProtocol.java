package org.dragons.itsmeim.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dragons.itsmeim.common.ContactId;
import org.dragons.itsmeim.common.events.*;
import org.dragons.itsmeim.model.conversationprotocol.words.ConversationProtocolWord;
import org.dragons.itsmeim.model.conversationprotocol.words.ImOnlineConversationProtocolWord;
import org.dragons.itsmeim.model.conversationprotocol.words.KeepAliveConversationProtocolWord;
import org.dragons.itsmeim.model.conversationprotocol.words.MessageConversationProtocolWord;
import org.dragons.itsmeim.model.conversationprotocol.words.WhosConversationProtocolWord;
import org.dragons.itsmeim.model.event.ConnectionInformationEvent;
import org.dragons.itsmeim.model.event.ConversationKeepAliveEvent;
import org.dragons.itsmeim.model.event.ImOnlineEvent;
import org.dragons.itsmeim.model.exceptions.MessageNotConformingToProtocolException;

/**
 * Tłumaczy zdarzenia kontrolera na wiadomości protokołu komunikacyjnego.
 */
public class ConversationProtocol implements Protocol
{
	private final Map<Class<? extends ConversationProtocolWord>, MessageHandler> wordsStrategyMap;
	private final Map<Class<? extends ContactEvent>, EventHandler> eventsStrategyMap;

	public ConversationProtocol()
	{
		wordsStrategyMap = loadWordsStrategy();
		eventsStrategyMap = loadEventsStrategy();
	}

	/**
	 * Przetwarza wiadomość protokołu na zdarzenie.
	 */
	@Override
	public ContactEvent processInput(final Object o, final ContactId c)
			throws MessageNotConformingToProtocolException
	{
		if (o == null)
		{
			throw new NullPointerException("Wiadomość nie może być null!");
		}
		if (!(o instanceof ConversationProtocolWord))
		{
			throw new MessageNotConformingToProtocolException();
		}
		ConversationProtocolWord word = (ConversationProtocolWord) o;
		return wordsStrategyMap.get(word.getClass()).handle(word, c);
	}

	/**
	 * Generuje wiadomość na podstawie zdarzenia.
	 */
	@Override
	public Object generateOutput(final ContactEvent e, final ContactId c)
	{
		if (e == null)
		{
			throw new NullPointerException("Event nie moze by null!");
		}
		return eventsStrategyMap.get(e.getClass()).handle(e);
	}

	/**
	 * Przetwarza pierwszą wiadomość przychodzącą.
	 */
	@Override
	public ConnectionInformationEvent processFirstMessage(final Object o)
			throws MessageNotConformingToProtocolException
	{
		if (o == null)
		{
			throw new NullPointerException("Wiadomość nie może być null!");
		}
		if (!(o instanceof WhosConversationProtocolWord))
		{
			throw new MessageNotConformingToProtocolException();
		}
		WhosConversationProtocolWord word = (WhosConversationProtocolWord) o;
		return new ConnectionInformationEvent(new ContactId(word.getName()),
				word.getAddress(), word.getPort());
	}

	/**
	 * Generuje pierwszą wiadomość jaką należy wysłać.
	 */
	@Override
	public Object generateFirstMessage(final ConnectionInformationEvent e)
	{
		return new WhosConversationProtocolWord(e.getContactId().getName(),
				e.getAddress(), e.getPort());
	}

	/**
	 * Obsługuje wiadomości.
	 */
	private abstract class MessageHandler
	{
		public abstract ContactEvent handle(
				final ConversationProtocolWord protocolWord,
				final ContactId contactId);
	}

	/**
	 * Nową wiadomość tłumaczy na zdarzenie otrzymania nowej wiadomości.
	 */
	private class MessageConversationProtocolWordHandler extends MessageHandler
	{

		@Override
		public ContactEvent handle(ConversationProtocolWord protocolWord,
				ContactId contactId)
		{
			final MessageConversationProtocolWord message = (MessageConversationProtocolWord) protocolWord;
			return new MessageFromEvent(contactId, message.getTime(),
					message.getMessage());
		}

	}

	/**
	 * Otrzymano keepalive.
	 */
	private class KeepAliveConversationProtocolWordHandler extends
			MessageHandler
	{

		@Override
		public ContactEvent handle(ConversationProtocolWord protocolWord,
				final ContactId contactId)
		{
			System.out.println("Keep alive from " + contactId);
			return new ConversationKeepAliveEvent(contactId);
		}

	}

	/**
	 * Kontakt zgłosił swoją dostępność.
	 */
	private class ImOnlineConversationProtocolWordHandler extends
			MessageHandler
	{

		@Override
		public ContactEvent handle(ConversationProtocolWord protocolWord,
				ContactId contactId)
		{
			return new ContactIsOnlineEvent(contactId);
		}

	}

	/**
	 * Tworzy mapę strategii dla wiadomości protokołu.
	 * 
	 * @return Niemodyfikowalna mapa strategii.
	 */
	private Map<Class<? extends ConversationProtocolWord>, MessageHandler> loadWordsStrategy()
	{
		Map<Class<? extends ConversationProtocolWord>, MessageHandler> tempWordsMap = new LinkedHashMap<Class<? extends ConversationProtocolWord>, MessageHandler>();
		tempWordsMap.put(MessageConversationProtocolWord.class,
				new MessageConversationProtocolWordHandler());
		tempWordsMap.put(KeepAliveConversationProtocolWord.class,
				new KeepAliveConversationProtocolWordHandler());
		tempWordsMap.put(ImOnlineConversationProtocolWord.class,
				new ImOnlineConversationProtocolWordHandler());
		return Collections.unmodifiableMap(tempWordsMap);

	}

	/**
	 * Obsługuję zdarzenia.
	 */
	private abstract class EventHandler
	{
		public abstract Object handle(final ContactEvent event);
	}

	/**
	 * Wiadomość do kontaktu.
	 * 
	 */
	private class MessageToEventHandler extends EventHandler
	{

		@Override
		public Object handle(ContactEvent event)
		{
			final MessageToEvent message = (MessageToEvent) event;
			return new MessageConversationProtocolWord(message.getTime(),
					message.getMessage());
		}

	}

	/**
	 * Keepalive.
	 */
	private class ConversationKeepAliveEventHandler extends EventHandler
	{

		@Override
		public Object handle(ContactEvent event)
		{
			System.out.println("Keep alive to " + event.getContactId());
			return new KeepAliveConversationProtocolWord();
		}

	}

	/**
	 * Poinformowanie kontaktu o dostępności.
	 */
	private class ImOnlineEventHandler extends EventHandler
	{

		@Override
		public Object handle(ContactEvent event)
		{
			return new ImOnlineConversationProtocolWord();
		}

	}

	/**
	 * Tworzy mapę strategii dla zdarzeń programu.
	 * 
	 * @return Niemodyfikowalna mapa strategii.
	 */
	private Map<Class<? extends ContactEvent>, EventHandler> loadEventsStrategy()
	{
		Map<Class<? extends ContactEvent>, EventHandler> tempEventsMap = new LinkedHashMap<Class<? extends ContactEvent>, EventHandler>();
		tempEventsMap.put(MessageToEvent.class, new MessageToEventHandler());
		tempEventsMap.put(ConversationKeepAliveEvent.class,
				new ConversationKeepAliveEventHandler());
		tempEventsMap.put(ImOnlineEvent.class, new ImOnlineEventHandler());
		return Collections.unmodifiableMap(tempEventsMap);
	}
}