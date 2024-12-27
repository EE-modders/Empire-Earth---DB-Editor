package EEmodders.datmanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import EEmodders.datstructure.DatStructure;
import EEmodders.gui.MainFrame;
import EEmodders.gui.scenes.DBSelectorController;
import javafx.application.Application;
import javafx.application.Platform;
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
	private static final String editorVersion = "DB Editor: \t\t\t" + Settings.VERSION + "\n";
	private static final String v_databaseVersion = "EEC database version: \t" + Settings.base_DBVersion + "\n";
	private static final String aoc_databaseVersion = "AoC database version: \t" + Settings.AoC_DBVersion + "\n";
	private static final String javaVersion = "Java version: \t\t\t" + System.getProperty("java.version") + "\n";

	private static final String popupText = editorVersion + v_databaseVersion + aoc_databaseVersion + javaVersion;

	private static DBSelectorController dbSelectorController;
	private static Stage stage;

	/** If true, the editor is in AOC mode */
	private static boolean bAOC = false;
	private static final File dataDirectory = Paths.get("EEEditorData").toFile();
	
	public static File getDataDirectory() { return new File(dataDirectory, Core.isAOC() ? "AOC" : "Vanilla"); }
	/**
	 * Check if the editor is in AOC mode
	 *
	 * @return true if editor is in AOC mode, false otherwise
	 */
	public static boolean isAOC() { return bAOC; }
	public static Stage getStage() { return stage; }
	public static DBSelectorController getDbSelectorController() { return dbSelectorController; }

	@Override
	public void start(Stage stage) throws IOException {
		Core.stage = stage;

		final var splashScreen = MainFrame.instance;
		splashScreen.initGUI();

		if (!supportedJava()) {
			JOptionPane.showMessageDialog(splashScreen,
					"You are using a not supported version of Java!\n\n"
							+"this can cause problems and bugs \n"
							+"please consider updating to Java 11 or up",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}

		versionSelector();
		DatStructure.initAllStructures(bAOC);

		// This makes the Language class initialize in background... SSSHHH!!!
		final var languageThread = new Thread(Language::updateLanguages);
		languageThread.start();

		dbSelector();

		try {
			languageThread.join();
		} catch (final InterruptedException e) {
			Util.printException(splashScreen, e, true);
			System.exit(0);
		}

		splashScreen.setVisible(false);
	}

	private void dbSelector() throws IOException {
		final var fxmlLoader = new FXMLLoader(DBSelectorController.class.getResource("dbSelector.fxml"));
		final var scene = new Scene(fxmlLoader.load());
		final var aoc = (bAOC ? "AOC" : "EEC");

		DBSelectorController controller = fxmlLoader.getController();
		dbSelectorController = controller;
		controller.setLoadDBButton("Select DAT folder for "+aoc);
		controller.setVersionLabel(Settings.VERSION);
		controller.setCreditLabel(Settings.DB_ICON_CREDIT);

		stage.setTitle(Settings.NAME +" ("+aoc+")");
		stage.getIcons().add(Util.getDBEditorIcon());
		stage.setScene(scene);
		stage.show();
	}

	private void versionSelector() {
		var question = new Alert(Alert.AlertType.CONFIRMATION);
		Util.setAlertIcon(question);
		question.setTitle(Settings.NAME);
		question.setHeaderText("EE Classic or EE AoC?");

		var btnEEC = new ButtonType("EE Classic");
		var btnAOC = new ButtonType("AOC");
		var btnExit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

		question.getButtonTypes().setAll(btnEEC, btnAOC, btnExit);

		var result = question.showAndWait();

		if (result.get() != btnExit) {
			bAOC = result.get() == btnAOC;
		} else {
			Core.exit();
		}
	}

	public static void showAbout() {
		var popup = new Alert(Alert.AlertType.INFORMATION);
		popup.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		Util.setAlertIcon(popup);
		popup.setTitle("About");
		popup.setHeaderText("Version Information");

		final String about = popupText
				+ "----------------------------------------------------\n"
				+ "Created by Forlins & Empire Earth Reborn Community\n"
				+ "Published under GPLv3 " + Settings.VERSION_YEAR;

		popup.setContentText(about);
		popup.showAndWait();
	}

	public static void showMissingFilesError() {
		var error = new Alert(Alert.AlertType.ERROR);

		Util.setAlertIcon(error);
		error.setTitle("Missing control files");
		error.setHeaderText("EEEditorData could not be found!");
		error.setContentText("Please make sure, you have EEEditorData folder \n next to the .jar file and then try again!");
		error.showAndWait();
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

	public static boolean checkUnsaved() {
		final var unsaved = DatFile.LOADED.stream()
				.filter(DatFile::isLoaded).filter(DatFile::isUnsaved)
				.collect(Collectors.toSet());

		if (unsaved.isEmpty())
			return true;

		var confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
		Util.setAlertIcon(confirmDialog);
		confirmDialog.setTitle(Settings.NAME);
		confirmDialog.setHeaderText("There are unsaved changes. Do you want to save all unsaved files?");

		var btnSave = new ButtonType("Save", ButtonBar.ButtonData.YES);
		var btnUnsave = new ButtonType("Don't save", ButtonBar.ButtonData.NO);
		var btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

		confirmDialog.getButtonTypes().setAll(btnSave, btnUnsave, btnCancel);

		var result = confirmDialog.showAndWait();
		var choice = result.get();

		if (choice == btnSave)
			unsaved.forEach(datfile -> datfile.saveFile(MainFrame.instance));
		else if (choice == btnCancel)
			return false;

		return true;
	}

	public static void exit() {
		if (!Core.checkUnsaved())
			return;

		Platform.exit();
		System.exit(0);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		Core.exit();
	}
}
