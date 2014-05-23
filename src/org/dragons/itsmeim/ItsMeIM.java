package org.dragons.itsmeim;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.dragons.itsmeim.common.events.Event;
import org.dragons.itsmeim.controller.Controller;
import org.dragons.itsmeim.model.Model;
import org.dragons.itsmeim.view.View;

/**
 * Uruchamia program.
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
