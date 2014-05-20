package pl.michal.debski.itsmeim.view;

/**
 * Interface nasłuchujący zdarzenia z okna logowania.
 * 
 * @author Michał Dębski
 */
public interface LoginWindowListener
{
	/**
	 * Użytkownik chce się zalogować.
	 * 
	 * @param lw
	 */
	public void logIn(LoginWindow lw);

	/**
	 * Użytkownik chce wyjść.
	 * 
	 * @param lw
	 */
	public void exit(LoginWindow lw);
}
