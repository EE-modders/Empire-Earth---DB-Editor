package gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import datmanager.Core;
import datmanager.Settings;
import datmanager.Util;
import gui.components.JImagePanel;

/**
 * The program splash screen
 *
 * @author MarcoForlini
 */
public class EESplashScreen extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final BufferedImage image = Util.readBufferedImage(GUI.class.getResource("DBE_icon.png"));
	private static final ImageIcon IMAGE_ICON = new ImageIcon(GUI.class.getResource("DBE_icon.png"));

	private final JImagePanel imagePanel = new JImagePanel(image);
	private final JLabel labelVersion = new JLabel(" Version: "+Settings.VERSION);
	private final JLabel imageCredit = new JLabel("Icon created by Fortuking ");

	/**
	 * Creates a new {@link EESplashScreen}
	 */
	public EESplashScreen() {
		initGUI();
	}

	private void initGUI() {
		setTitle(Core.titleText);
		if (IMAGE_ICON != null)
			setIconImage(IMAGE_ICON.getImage());

		imagePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		setContentPane(imagePanel);

		if (image != null) {
			setSize(image.getWidth(), image.getHeight()); // (600, 237);
		} else {
			setSize(5, 5);
		}
		setLocationRelativeTo(null);
		setUndecorated(true);

		imagePanel.setLayout(new BorderLayout(0, 0));
		imagePanel.add(labelVersion, BorderLayout.NORTH);
		imagePanel.add(imageCredit, BorderLayout.SOUTH);

		labelVersion.setHorizontalAlignment(SwingConstants.LEFT);
		imageCredit.setHorizontalAlignment(SwingConstants.RIGHT);
	}
}
