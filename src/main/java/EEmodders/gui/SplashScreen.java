package EEmodders.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import EEmodders.datmanager.Settings;
import EEmodders.datmanager.Util;
import EEmodders.gui.components.JImagePanel;

/**
 * Splash screen
 */
public class SplashScreen extends JFrame {

	public SplashScreen() {
		super(Settings.NAME);
		initGUI();
	}

	private void initGUI() {
		var icon = Util.readBufferedImage(GUI.class.getResource(Settings.DB_ICON));

		setIconImage(icon);

		if (icon != null) {
			setSize(icon.getWidth(), icon.getHeight()); // (600, 237);
		} else {
			setSize(5, 5);
		}
		setLocationRelativeTo(null);
		setUndecorated(true);

		var imagePanel = new JImagePanel(icon);
		var iconCredit = new JLabel(Settings.DB_ICON_CREDIT);
		var labelVersion = new JLabel(Settings.VERSION);

		labelVersion.setHorizontalAlignment(SwingConstants.CENTER);
		iconCredit.setHorizontalAlignment(SwingConstants.CENTER);

		imagePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		imagePanel.setLayout(new BorderLayout(0, 0));

		imagePanel.add(labelVersion, BorderLayout.SOUTH);
		imagePanel.add(iconCredit, BorderLayout.NORTH);

		setContentPane(imagePanel);
	}
}
