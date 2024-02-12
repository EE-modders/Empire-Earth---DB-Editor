package EEmodders.Utils;

import EEmodders.Main;
import EEmodders.datmanager.Settings;
import EEmodders.datmanager.StringIterator;
import EEmodders.gui.GUI;
import EEmodders.gui.components.JScrollPaneRed;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public final class Util {

	/** Convert a float number to string with a specific number of decimals and round */
	public static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);

	static {
		Util.numberFormat.setMinimumFractionDigits(1);
		Util.numberFormat.setMaximumFractionDigits(6);
		Util.numberFormat.setGroupingUsed(false);
		Util.numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
	}

	/** No need to instantiate this */
	private Util() {
	}

	/**
	 * Select a directory, starting from the given one
	 *
	 * @param parent            The parent component
	 * @param startingDirectory The starting directory. If null, starts from the current directory
	 * @return The choosen directory
	 */
	public static File selectDBDirectory(Component parent, File startingDirectory) {
		final JFileChooser fileChooser = new JFileChooser(startingDirectory != null ? startingDirectory : new File("."));
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setDialogTitle("Select the folder which contains the EE dat files");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

		final int response = fileChooser.showDialog(parent, "Select directory");
		if (response == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}

		return null;
	}

	public static void setAlertIcon(Alert alert) {
		var alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		alertStage.getIcons().add(getDBEditorIcon());
	}

	public static File getDBDirectory(File startingDir) {
		var dirChooser = new DirectoryChooser();

		if (startingDir != null && startingDir.exists())
			dirChooser.setInitialDirectory(startingDir);

		dirChooser.setTitle("Select the folder which contains the EE dat files");

		File dbDir = dirChooser.showDialog(Main.getStage());

		return dbDir;
	}

	public static Image getDBEditorIcon() {
		return new Image(Objects.requireNonNull(GUI.class.getResource("DBE_icon.png")).toString());
	}
	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text, 10);
			return true;
		} catch (final Throwable e) {
			return false;
		}
	}

	public static boolean isFloat(String text) {
		try {
			Float.parseFloat(text);
			return true;
		} catch (final Throwable e) {
			return false;
		}
	}

	public static List<String> split(String text, char separator) {
		return split_internal(new StringIterator(text, separator));
	}

	public static List<String> split(String text, String separator) {
		return split_internal(new StringIterator(text, separator));
	}

	private static List<String> split_internal(Iterator<String> it) {
		final List<String> list = new ArrayList<>();
		it.forEachRemaining(elem -> {
			if (elem != null && !elem.isBlank()) {
				list.add(elem);
			}
		});
		return list;
	}

	public static boolean matchPatterns(String text, List<String> patterns) {
		int pos = 0;
		for (final String pattern : patterns) {
			final int next = text.indexOf(pattern, pos);
			if (next >= 0) {
				pos = next + pattern.length();
			} else {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean containsAny(Collection<T> c1, Collection<? extends T> c2) {
		for (final T t : c2) {
			if (c1.contains(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert a throwable's stack trace to String
	 *
	 * @param e The throwable
	 * @return Its stack trace
	 */
	public static String buildStackTrace(Throwable e) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	/**
	 * Show a message about an error and ask the user to see the stack trace of the given exception
	 *
	 * @param parent             The parent component
	 * @param e                  The exception
	 * @param message            Message to display
	 * @param title              Title of the message
	 * @param alsoPrintToConsole if <code>true</code>, also print the stack trace to console
	 */
	public static void printException(Component parent, Throwable e, String message, String title, boolean alsoPrintToConsole) {
		final String[] exceptionChoices = { "Close", "Show stack trace" };
		if (JOptionPane.showOptionDialog(parent, message, title, 0, JOptionPane.ERROR_MESSAGE, null, exceptionChoices, exceptionChoices[0]) == 1) {
			Util.printException(parent, e, alsoPrintToConsole);
		}
	}

	/**
	 * Show a message with the stack trace of the given exception
	 *
	 * @param parent             The parent component
	 * @param e                  The exception
	 * @param alsoPrintToConsole if <code>true</code>, also print the stack trace to console
	 */
	public static void printException(Component parent, Throwable e, boolean alsoPrintToConsole) {
		if (alsoPrintToConsole) {
			e.printStackTrace();
		}
		final JTextArea area = new JTextArea(buildStackTrace(e));
		area.setForeground(Color.RED);
		final JScrollPane scrollPane = new JScrollPaneRed(area);
		scrollPane.setPreferredSize(new Dimension(800, 500));
		Util.printError(parent, scrollPane, "Exception: stack trace");
	}

	/**
	 * Show a message about an error
	 *
	 * @param parent  The parent component
	 * @param message Message to display
	 * @param title   Title of the message
	 */
	public static void printError(Component parent, Object message, String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show a message about a warning
	 *
	 * @param parent  The parent component
	 * @param message Message to display
	 * @param title   Title of the message
	 */
	public static void printWarning(Component parent, Object message, String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static BufferedImage readBufferedImage(URL url) {
		try {
			return ImageIO.read(url);
		} catch (final IOException e) {
			printException(null, e, "Error while loading the image: " + url.getFile(), "Error", true);
			return null;
		}
	}

	public static void openEESUrl() {
		try {
			Desktop.getDesktop().browse(new URI(Settings.EES_URL));
		} catch (Exception ignore) {}
	}

	public static String fromCString(byte[] data) {
		int index = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0) {
				index = i;
				break;
			}
		}
		return new String(Arrays.copyOfRange(data, 0, index));
	}

	public static byte[] toCString(String string, int maxSize) {
		byte[] data = string.getBytes(StandardCharsets.US_ASCII);
		return Arrays.copyOf(data, maxSize);
	}
}
