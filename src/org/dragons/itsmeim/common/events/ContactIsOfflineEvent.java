package org.dragons.itsmeim.common.events;

import org.dragons.itsmeim.common.ContactId;

/**
 * Kontakt jest niedostępny.
 */
public class ContactIsOfflineEvent extends ContactEvent
{

	public ContactIsOfflineEvent(ContactId c)
	{
		super(c);
	}
}
