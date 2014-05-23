package org.dragons.itsmeim.view;

/**
 * Interface do obsługi zdarzeń z okna dodawania nowego kontaktu.
 */
public interface AddContactWindowListener
{
	/**
	 * Wpisany kontakt został zaakceptowany przez użytkownika.
	 * 
	 * @param acw
	 */
	public void confirmed(AddContactWindow acw);

	/**
	 * Wpisywanie kontaktu zostało przerwane.
	 * 
	 * @param acw
	 */
	public void canceled(AddContactWindow acw);

	/**
	 * Nastąpiła zmiana we wprowadzanych danych.
	 * 
	 * @param acw
	 */
	public void inputChanged(AddContactWindow acw);
}
