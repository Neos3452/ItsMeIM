package pl.michal.debski.itsmeim.view;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import static pl.michal.debski.itsmeim.view.ViewContactState.*;

/**
 * Ta klasa nie jest skończona i jest eksperymentalnym dodatkiem.
 * 
 * @author Michał Dębski
 * 
 */
public class ContactChangedStatePopup
{
	private final Timer timer = new Timer();
	private final JFrame frame = new JFrame();
	private JLabel icon;
	private JLabel text;
	{
		frame.setUndecorated(true);
		frame.setFocusable(false);
		frame.setAlwaysOnTop(true);
		try
		{
			Image logo = ImageIO.read(this.getClass().getResource("logo.png"))
					.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			icon = new JLabel(new ImageIcon(logo));
			frame.add(icon, BorderLayout.WEST);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		// frame.setResizable(false);
	}

	public ContactChangedStatePopup(ViewContact contact)
	{
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
		frame.add(text);
		frame.pack();
		frame.setVisible(true);
		timer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				SwingUtilities.invokeLater(new Runnable()
				{

					@Override
					public void run()
					{
						ContactChangedStatePopup.this.timer.cancel();
						ContactChangedStatePopup.this.frame.setVisible(false);
						ContactChangedStatePopup.this.frame.dispose();
					}
				});
			}
		}, 5000);
	}
}
