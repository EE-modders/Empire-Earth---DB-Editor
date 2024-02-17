package EEmodders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.swing.*;

import EEmodders.datmanager.DatFile;
import EEmodders.datmanager.Language;
import EEmodders.datmanager.Settings;
import EEmodders.Utils.Util;
import EEmodders.gui.SplashScreen;
import EEmodders.gui.scenes.MainWindowController;
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
 */
public class Main extends Application {

	// TODO remove this nonsense
	public static final JFrame awtRoot = new JFrame();
	private static Stage stage;

	private static final File dataDirectory = Paths.get("EEEditorData").toFile();
	
	public static File getDataDirectory() { return new File(dataDirectory, "Vanilla"); }

	public static Stage getStage() { return stage; }

	// TODO remove this nonsense
	public static boolean isAOC() {
		return false;
	}

	@Override
	public void start(Stage stage) throws IOException {
		var splashScreen = new SplashScreen(Settings.NAME);
		splashScreen.setVisible(true);
		Main.stage = stage;

		splashScreen.setStatusLabel("init dat structures");
		//DatStructure.initAllStructures(bAOC);

		// This makes the Language class initialize in background... SSSHHH!!!
		final var languageThread = new Thread(Language::updateLanguages);
		languageThread.start();

		splashScreen.setStatusLabel("loading main UI");
		initMainWindow(stage);

		try {
			languageThread.join();
		} catch (final InterruptedException e) {
			Util.printException(splashScreen, e, true);
			System.exit(0);
		}

		splashScreen.setVisible(false);
		splashScreen.dispose();
	}

	private void initMainWindow(Stage stage) throws IOException {
		var fxmlLoader = new FXMLLoader(MainWindowController.class.getResource("MainWindow.fxml"));
		var scene = new Scene(fxmlLoader.load());

		MainWindowController controller = fxmlLoader.getController();
		controller.initUI(stage);

		stage.setScene(scene);
		stage.show();
	}

	public static void showAbout() {
		var popup = new Alert(Alert.AlertType.INFORMATION);
		popup.getDialogPane().setMinSize(400, Region.USE_PREF_SIZE);
		Util.setAlertIcon(popup);
		popup.setTitle(Settings.NAME);
		popup.setHeaderText("About "+Settings.NAME);

		String aboutText =
				"""
				DB Editor:	%s
				EEC DB:		%s
				AOC DB:		%s
				Java:		%s
				------------------------------------------------------------------------------------------
				Created by Forlins & zocker_160 from Empire Earth: Reborn
				Licensed under GPLv3 | %s
				""".formatted(
				Settings.VERSION,
				Settings.base_DBVersion,
				Settings.AoC_DBVersion,
				System.getProperty("java.version"),
				Settings.VERSION_YEAR
		);

		popup.setContentText(aboutText);
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
			unsaved.forEach(datfile -> datfile.saveFile(awtRoot));
		else if (choice == btnCancel)
			return false;

		return true;
	}

	public static void exit() {
		if (!Main.checkUnsaved())
			return;

		Platform.exit();
		System.exit(0);
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		Main.exit();
	}

	/**
	 * Launcher class, stupid workaround of a fk stupid JavaFX issue, where FX fails to load for some reason
	 */
	public static class Launcher {
		public static void main(String[] args) {
			Application.launch(Main.class, args);
		}
	}
}
