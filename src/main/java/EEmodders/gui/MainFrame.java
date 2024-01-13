package EEmodders.gui;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import EEmodders.datmanager.Core;
import EEmodders.datmanager.DatFile;
import EEmodders.datmanager.Settings;
import EEmodders.datmanager.Util;
import EEmodders.datstructure.DatStructure;
import EEmodders.gui.components.JImagePanel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 * The program main frame including splash screen
 */
public class MainFrame extends JFrame {

	public static final MainFrame instance = new MainFrame();

	private static final long serialVersionUID = 1973882004055163035L;

	public MainFrame() {}

	public void initGUI() {
		final var icon = Util.readBufferedImage(GUI.class.getResource(Settings.DB_ICON));

		setTitle(Settings.NAME);
		setIconImage(icon);

		if (icon != null) {
			setSize(icon.getWidth(), icon.getHeight()); // (600, 237);
		} else {
			setSize(5, 5);
		}
		setLocationRelativeTo(null);
		setUndecorated(true);

		final var imagePanel = new JImagePanel(icon);
		final var iconCredit = new JLabel(Settings.DB_ICON_CREDIT);
		final var labelVersion = new JLabel(" Version: "+Settings.VERSION);

		iconCredit.setHorizontalAlignment(SwingConstants.RIGHT);
		labelVersion.setHorizontalAlignment(SwingConstants.LEFT);

		imagePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		imagePanel.setLayout(new BorderLayout(0, 0));

		imagePanel.add(labelVersion, BorderLayout.NORTH);
		imagePanel.add(iconCredit, BorderLayout.SOUTH);

		setContentPane(imagePanel);

		setVisible(true);
	}

	/**
	 * Load all selected files from given directory
	 *
	 * @param directory directory to load dat files from
	 */
	public void loadFiles(File directory) {
		final List<DatFile> files = getDatFilesFromDirectory(directory);

		if (files != null)
			loadFiles(files);
	}

	private List<DatFile> getDatFilesFromDirectory(final File directory) {
		List<DatFile> allFiles;

		allFiles = Arrays.stream(DatStructure.GetLoadedStructures())
				.map(datStructure -> new DatFile(directory, datStructure))
				.filter(DatFile::exists)
				.toList();

		if (allFiles.isEmpty()) {
			var alert = new Alert(Alert.AlertType.ERROR);
			Util.setAlertIcon(alert);
			alert.setTitle("Error");
			alert.setHeaderText("There are no valid .dat files in the selected directory!");
			alert.setContentText("Selected directory: "+directory);
			alert.showAndWait();
			return null;
		}

		return allFiles;
	}

	/**
	 * Load the given list of files and disable (but not freeze) the calling window until finished.
	 *
	 * @param filesToLoad   The files to load
	 */
	private void loadFiles(List<DatFile> filesToLoad) {
		final var progressDialog = new DialogProgressBar("Loading...", filesToLoad.size(), true);

		final List<LoadThread> threads = new ArrayList<>(filesToLoad.size());
		for (int i = 0; i < filesToLoad.size(); i++) {
			final var thread = new LoadThread(filesToLoad.get(i), i, progressDialog::updatePercPart);
			thread.start();
			threads.add(thread);
		}
		// loadError(datFile, e);

		try {
			final var maxTime = System.currentTimeMillis() + Settings.LOAD_MAX_WAIT;
			for (final var thread : threads) {
				while (thread.isAlive() && System.currentTimeMillis() <= maxTime) {
					Thread.sleep(50);
				}
			}
		} catch (final InterruptedException e) {
			Util.printException(this, e, true);
			return;
		}

		if (progressDialog.isDisplayable()) {
			progressDialog.dispose();
		}

		final List<DatFile> filesNotLoaded = threads.stream()
				.filter(LoadThread::isFailed)
				//.peek(t -> showLoadError(t.getDatFile(), t.getError()))
				.map(LoadThread::getDatFile)
				.collect(Collectors.toList());

		final Map<DatFile, Throwable> mapNotLoaded = threads.stream()
				.filter(LoadThread::isFailed)
				.collect(Collectors.toMap(LoadThread::getDatFile, LoadThread::getError));

		if (!mapNotLoaded.isEmpty()) {
			showLoadError(mapNotLoaded);
			return;
		}

		final List<DatFile> filesLoaded = threads.stream()
				.filter(LoadThread::isCompleted)
				.map(LoadThread::getDatFile)
				.collect(Collectors.toList());

		if (filesLoaded.size() > 0) {
			onLoadSucceed(filesLoaded);
		}
	}

	private void onLoadSucceed(Collection<DatFile> loaded) {
		DatFile.LOADED.addAll(loaded);
		DatFile.LOADED.forEach(DatFile::buildLinks);
		DatFile.LOADED.forEach(df -> df.dummyEntryGroup.sort(null));

		List<DatFile> loadedDBs = DatFile.LOADED.stream().sorted().collect(Collectors.toList());
		Core.getDbSelectorController().setDBButtons(loadedDBs);
	}

	private void showLoadError(Map<DatFile, Throwable> notLoaded) {
		var errorMessage = new Alert(Alert.AlertType.ERROR);
		errorMessage.getDialogPane().setMinSize(400, Region.USE_PREF_SIZE);
		Util.setAlertIcon(errorMessage);
		errorMessage.setTitle("ERROR");
		errorMessage.setHeaderText("An error occurred during the loading of DAT files!");

		var coreMessage = new StringBuilder(
				"Make sure that you: \n\n" +
				"- unpacked and decompressed the data.ssa correctly \n" +
				"- use files supported by DB Editor \n" +
				"- selected the correct folder \n" +
				"- selected the correct game version (EEC/AOC) \n" +
				"\n" +
				"You can use EE Studio II for unpacking SSA files. \n" +
				"------------------------------------------------ \n" +
				"Following files failed to load: \n");

		for (var dat : notLoaded.keySet())
			coreMessage.append("- ").append(dat.getName()).append("\n");

		errorMessage.setContentText(coreMessage.toString());

		var btnEES = new ButtonType("EE Studio II");
		var btnTrace = new ButtonType("Stacktraces", ButtonBar.ButtonData.LEFT);
		var btnClose = new ButtonType("Close", ButtonBar.ButtonData.NEXT_FORWARD);
		var btnExit = new ButtonType("Exit", ButtonBar.ButtonData.LEFT);

		errorMessage.getButtonTypes().setAll(btnExit, btnTrace, btnEES, btnClose);

		var result = errorMessage.showAndWait();
		var choice = result.get();

		if (choice == btnEES) {
			new Thread(Util::openEESUrl).start();
		} else if (choice == btnTrace) {
			for (var error : notLoaded.values())
				Util.printException(null, error, false);
		} else if (choice == btnExit) {
			Core.exit();
		}
	}
}
