package org.dragons.itsmeim.view;

import static org.dragons.itsmeim.view.ViewContactState.OFF;
import static org.dragons.itsmeim.view.ViewContactState.ON;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.dragons.itsmeim.common.ContactId;

/**
 * Klasa testowa!
 */
public class RollupPopup
{
	private static final int TIMEOUT = 40;
	private JFrame frame = new JFrame();
	private JPanel container = new JPanel(new BorderLayout());
	private JLabel icon = null;
	private JLabel text;
	@SuppressWarnings("unused")
	private long rollinTime;
	private long stayTime;
	@SuppressWarnings("unused")
	private long rolloutTime;
	private long stepin;
	private long stepout;
	private int y;
	private Timer timer = new Timer();
	private TimerTask rollin = new TimerTask()
	{
		@Override
		public void run()
		{
			if (y <= 0)
				rollinEnd();
			container.setLocation(container.getX(), y--);
		}
	};
	private TimerTask rollout = new TimerTask()
	{

		@Override
		public void run()
		{
			if (y <= -container.getHeight())
				stop();
			container.setLocation(container.getX(), y--);
		}
	};

	private RollupPopup(ViewContact contact, long rollinTime, long stayTime,
			long rolloutTime)
	{
		this.rollinTime = rollinTime;
		this.stayTime = stayTime;
		this.rolloutTime = rolloutTime;
		container
				.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setFocusable(false);
		frame.setAlwaysOnTop(true);
		frame.setLayout(null);
		try
		{
			Image logo = ImageIO.read(this.getClass().getResource("logo.png"))
					.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			icon = new JLabel(new ImageIcon(logo));
			// frame.add(icon, BorderLayout.WEST);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		String title;
		if (contact.getState().equals(ON))
			title = contact.getDisplayName() + " went online.";
		else if (contact.getState().equals(OFF))
			title = contact.getDisplayName() + " went offline.";
		else
		{
			frame.dispose();
			return;
		}
		text = new JLabel(title);
		if (icon != null)
			container.add(icon, BorderLayout.WEST);
		container.add(text, BorderLayout.EAST);
		container.validate();
		Dimension size = container.getPreferredSize();
		y = size.height;
		stepin = (size.height / (rollinTime / TIMEOUT));
		if (stepin <= 0)
			stepin = 1;
		stepout = (size.height / (rolloutTime / TIMEOUT));
		if (stepout <= 0)
			stepout = 1;
		container.setBounds(0, size.height, size.width, size.height);
		frame.setSize(size);
		frame.add(container);
		container.repaint();
	}

	private void start()
	{
		System.out.println("start");
		frame.setVisible(true);
		timer.schedule(rollin, TIMEOUT, TIMEOUT);
	}

	private void stop()
	{
		System.out.println("start");
		timer.cancel();
		frame.dispose();
	}

	private void rollinEnd()
	{
		System.out.println("start");
		rollin.cancel();
		timer.schedule(rollout, stayTime, TIMEOUT);
	}

	public static void main(String[] args)
	{
		new RollupPopup(new ViewContact(new ContactId("AlaId"), ON, "AlaDisp"),
				2000, 2000, 2000).start();
	}
}
