package EEmodders.datmanager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

import EEmodders.datstructure.DatStructure;
import EEmodders.gui.EESplashScreen;
import EEmodders.gui.FrameMain;
import EEmodders.gui.scenes.DBSelectorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Core class. Contains the method main, the main data loaded by the program and some useful methods
 *
 * @author MarcoForlini
 */
public class Core extends Application {

	public static final String titleText = "Empire Earth - DB Editor";
	
	private static final String editorVersion = "DB Editor: \t\t\t" + Settings.VERSION + "\n";
	private static final String v_databaseVersion = "EEC database version: \t" + Settings.base_DBVersion + "\n";
	private static final String aoc_databaseVersion = "AoC database version: \t" + Settings.AoC_DBVersion + "\n";
	private static final String javaVersion = "Java version: \t\t\t" + System.getProperty("java.version") + "\n";

	private static final String popupText = editorVersion + v_databaseVersion + aoc_databaseVersion + javaVersion;

	private static DBSelectorController dbSelectorController;
	private static Stage stage;

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
	public static Stage getStage() { return stage; }
	public static DBSelectorController getDbSelectorController() { return dbSelectorController; }
	@Override
	public void start(Stage stage) throws IOException {
		Core.stage = stage;

		final var splashScreen = new EESplashScreen();
		//splashScreen.setVisible(true);

		if (!supportedJava()) {
			JOptionPane.showMessageDialog(splashScreen,
					"You are using a not supported version of Java!\n\n"
							+"this can cause problems and bugs \n"
							+"please consider updating to Java 11 or up",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}

		versionSelector();
		dbSelector();

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

		//splashScreen.setVisible(false);
		//FrameMain.instance.setVisible(true);

	}

	private void dbSelector() throws IOException {
		var fxmlLoader = new FXMLLoader(DBSelectorController.class.getResource("dbSelector.fxml"));
		var scene = new Scene(fxmlLoader.load());

		DBSelectorController controller = fxmlLoader.getController();
		dbSelectorController = controller;
		controller.setLoadDBButton("Select DAT folder for "+(AOC ? "AOC" : "EEC"));
		controller.setVersionLabel(Settings.VERSION);

		stage.setTitle(titleText);
		stage.getIcons().add(Util.getDBEditorIcon());
		stage.setScene(scene);
		stage.show();
	}
	private void versionSelector() {
		var question = new Alert(Alert.AlertType.CONFIRMATION);
		Util.setAlertIcon(question);
		question.setTitle(titleText);
		question.setHeaderText("EE Classic or EE AoC?");

		var btnEEC = new ButtonType("EE Classic");
		var btnAOC = new ButtonType("AOC");
		var btnExit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

		question.getButtonTypes().setAll(btnEEC, btnAOC, btnExit);

		var result = question.showAndWait();

		if (result.get() != btnExit) {
			AOC = result.get() == btnAOC;
		} else {
			System.exit(0);
		}
	}

	public static void showInfo() {
		var popup = new Alert(Alert.AlertType.INFORMATION);
		Util.setAlertIcon(popup);
		popup.setTitle("About");
		popup.setHeaderText("Version Information");

		final String about = popupText
				+ "----------------------------------------------------\n"
				+ "Created by Forlins & Empire Earth Reborn Community\n"
				+ "Published under GPLv3 " + Settings.VERSION_YEAR;

		popup.setContentText(about);

		popup.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		popup.showAndWait();
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
}
