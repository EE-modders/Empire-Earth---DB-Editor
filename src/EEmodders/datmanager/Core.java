package EEmodders.datmanager;

import java.io.File;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import EEmodders.datstructure.DatStructure;
import EEmodders.gui.EESplashScreen;
import EEmodders.gui.FrameMain;

/**
 * Core class. Contains the method main, the main data loaded by the program and some useful methods
 *
 * @author MarcoForlini
 */
public class Core {

	public static final String titleText = "Empire Earth - DB Editor";
	private static final String[] editorModeChoices = new String[] { "EE Classic", "Art of Conquest", "Exit" };
	
	private static final String editorVersion = "v" + Settings.VERSION + "\n";
	private static final String v_databaseVersion = "EEC database version: " + Settings.base_DBVersion + "\n";
	private static final String aoc_databaseVersion = "AoC database version: " + Settings.AoC_DBVersion + "\n";
	private static final String javaVersion = "Java version: " + System.getProperty("java.version") + "\n";
	private static final String questionText = "\nEEC or AoC?";

	private static final String popupText = editorVersion + v_databaseVersion + aoc_databaseVersion + javaVersion + questionText;
	
	/** If true, the editor is in AOC mode */
	private static boolean AOC = false;
	private static final File dataDirectory = Paths.get("EEEditorData").toFile();
	
	public static File getDataDirectory() { return new File(dataDirectory, Core.isAOC() ? "AOC" : "Vanilla"); }
	/**
	 * Check if the editor is in AOC mode
	 *
	 * @return true if editor is in AOC mode, false otherwise
	 */
	public static boolean isAOC() { return AOC; }

	public static void main(String[] args) {
		final EESplashScreen splashScreen = new EESplashScreen();
		splashScreen.setVisible(true);

		if (!supportedJava()) {
			JOptionPane.showMessageDialog(splashScreen,
					"You are using a not supported version of Java!\n\n"
							+"this can cause problems and bugs \n"
							+"please consider updating to Java 11 or up",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}

		switch (JOptionPane.showOptionDialog(splashScreen, popupText, titleText, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, editorModeChoices, editorModeChoices[0])) {
			case 0:
				AOC = false;
				break;
			case 1:
				AOC = true;
				break;
			case 2:
			case JOptionPane.CLOSED_OPTION:
				System.exit(0);
				break;
		}

		final var languageThread = new Thread(Language::updateLanguages); // This makes the Language class initialize in background... SSSHHH!!!
		final var datStructuresThread = new Thread(DatStructure::initAllStructures);

		languageThread.start();
		datStructuresThread.start();

		try {
			languageThread.join();
			datStructuresThread.join();
		} catch (final InterruptedException e) {
			Util.printException(splashScreen, e, true);
			System.exit(0);
		}

		splashScreen.setVisible(false);
		FrameMain.instance.setVisible(true);
	}

	private static boolean supportedJava() {
		String version = System.getProperty("java.version");
		
		if (version.startsWith("1.")) {
		    version = version.substring(2, 3);
		} else {
		    int dot = version.indexOf(".");
		    if (dot != -1) { version = version.substring(0, dot); }
		}

		return Integer.parseInt(version) >= 11;
	}

	/*
	// Some Windows bullshit nobody cares about
	private static void readGameFolder() {
		if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			// There is no registry on Linux, so just ignore
			System.out.println("Linux found, ignoring registry lookup.");
			return;
		}
		final String location = AOC ? "HKCU\\Software\\Mad Doc Software\\EE-AOC" : "HKCU\\Software\\SSSI\\Empire Earth";
		try {
			final Process processVolume = readRegistry(location, "Installed From Volume");
			final Process processDirectory = readRegistry(location, "Installed From Directory");
			processVolume.waitFor();
			processDirectory.waitFor();

			try (Scanner volumeScanner = new Scanner(processVolume.getInputStream());
				 Scanner directoryScanner = new Scanner(processDirectory.getInputStream())) {
				final var volume = volumeScanner.tokens().collect(Collectors.joining(" "));
				final var directory = directoryScanner.tokens().collect(Collectors.joining(" "));

				gameDirectory = volume.substring(volume.indexOf("REG_SZ") + 6).strip() + directory.substring(directory.indexOf("REG_SZ") + 6).strip();
				System.out.println("Found game base directory: " + gameDirectory);
			}
		} catch (final IOException | InterruptedException e) {
			System.out.println("Could not find game base directory using registry.");
		}
	}

	private static Process readRegistry(String location, String key) throws IOException {
		return Runtime.getRuntime().exec("reg query \"" + location + "\" /v \"" + key + "\"");
	}
	 */
}
