package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Kontakt jest dostępny.
 */
public class ContactIsOnlineEvent extends ContactEvent
{

	public ContactIsOnlineEvent(ContactId c)
	{
		super(c);
	}
}
