package pl.michal.debski.itsmeim.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import pl.michal.debski.itsmeim.model.exceptions.AlreadyInitatedConnectionException;
import pl.michal.debski.itsmeim.model.exceptions.NotInitatedConnectionException;

/**
 * Reprezentuje aktualnie działające połączenie.
 * 
 * @author Michał Dębski
 */
public class ActiveConnection
{
	private final Socket socket;
	private ObjectOutputStream objectOutputStream = null;
	private ObjectInputStream objectInputStream = null;
	private Date initTime;
	private Date lastUsedTime;

	/**
	 * Tworzy aktualne połączenie na podanym socketcie.
	 * 
	 * @param socket
	 */
	public ActiveConnection(final Socket socket)
	{
		this.socket = socket;
		lastUsedTime = initTime = Calendar.getInstance().getTime();
	}

	/**
	 * Inicjuje część połączenia odpowiedzialną za wysyłanie.
	 * 
	 * @throws IOException
	 */
	public void initOutput() throws IOException
	{
		if (objectOutputStream != null)
			throw new AlreadyInitatedConnectionException(
					"Output został już zainicalizowany!");
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		synchronized (lastUsedTime)
		{
			lastUsedTime = Calendar.getInstance().getTime();
		}
	}

	/**
	 * Inicjuje część połączenia odpowiedzialną za odbieranie.
	 * 
	 * @throws IOException
	 */
	public void initInput() throws IOException
	{
		if (objectInputStream != null)
		{
			throw new AlreadyInitatedConnectionException(
					"Input został już zainicalizowany!");
		}
		objectInputStream = new ObjectInputStream(socket.getInputStream());
		synchronized (lastUsedTime)
		{
			lastUsedTime = Calendar.getInstance().getTime();
		}
	}

	/**
	 * Wysyła podany obiekt tym połączeniem.
	 * 
	 * @param objectToSend
	 * @throws IOException
	 */
	public void sendObject(final Object objectToSend) throws IOException
	{
		if (objectOutputStream == null)
		{
			throw new NotInitatedConnectionException(
					"Output nie został zainicalizowany!");
		}
		synchronized (objectOutputStream)
		{
			objectOutputStream.writeObject(objectToSend);
			synchronized (lastUsedTime)
			{
				lastUsedTime = Calendar.getInstance().getTime();
			}
		}
	}

	/**
	 * Odbiera obiekt z tego połącznia, wątek blokuje się do momentu odebrania
	 * wiadomości.
	 * 
	 * @return Odebrany objekt.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object receiveObject() throws IOException, ClassNotFoundException
	{
		if (objectInputStream == null)
		{
			throw new NotInitatedConnectionException(
					"Input nie został zainicalizowany!");
		}
		synchronized (objectInputStream)
		{
			Object o = objectInputStream.readObject();
			synchronized (lastUsedTime)
			{
				lastUsedTime = Calendar.getInstance().getTime();
			}
			return o;
		}
	}

	/** Zamyka to połączenie. */
	public void close()
	{
		if (objectInputStream != null)
		{
			try
			{
				objectInputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if (objectOutputStream != null)
		{
			try
			{
				objectOutputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Zwraca czas nawiązania połączenia.
	 * 
	 * @return
	 */
	public Date getInitTime()
	{
		return initTime;
	}

	/**
	 * Zwraca czas ostatniego użycia tego połączenia.
	 * 
	 * @return
	 */
	public Date getLastUsedTime()
	{
		synchronized (lastUsedTime)
		{
			return lastUsedTime;
		}
	}
}
