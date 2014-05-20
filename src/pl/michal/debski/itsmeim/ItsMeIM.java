/**
 * 
 */
package pl.michal.debski.itsmeim;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.michal.debski.itsmeim.common.events.Event;
import pl.michal.debski.itsmeim.controller.Controller;
import pl.michal.debski.itsmeim.model.Model;
import pl.michal.debski.itsmeim.view.View;

/**
 * Uruchamia program.
 * 
 * @author Michał Dębski
 */
public class ItsMeIM
{

	/** @param args */
	public static void main(String[] args)
	{
		final BlockingQueue<Event> vbq = new LinkedBlockingQueue<Event>();
		final BlockingQueue<Event> mbq = new LinkedBlockingQueue<Event>();
		final BlockingQueue<Event> cbq = new LinkedBlockingQueue<Event>();
		final View view = new View(vbq);
		final Model model = new Model(mbq, cbq);
		final Controller controller = new Controller(vbq, view, mbq, cbq, model);
		controller.run();
	}

}
