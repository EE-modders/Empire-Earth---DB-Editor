package EEmodders;

import EEmodders.datmanager.DatFile;
import EEmodders.datmanager.Settings;
import EEmodders.datmanager.Util;
import EEmodders.datstructure.DatStructure;
import EEmodders.gui.DialogProgressBar;
import EEmodders.gui.LoadThread;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Core {
    private static final Core instance = new Core();

    public static Core getInstance() {
        return instance;
    }

    private Core() {}

    /**
     * Load all selected files from given directory
     *
     * @param directory directory to load dat files from
     */
    public List<DatFile> loadFiles(File directory) {
        final List<DatFile> files = getDatFilesFromDirectory(directory);

        if (files != null)
            return loadFiles(files);
        else
            return null;
    }

    private List<DatFile> getDatFilesFromDirectory(final File directory) {
        List<DatFile> allFiles = Arrays.stream(DatStructure.GetLoadedStructures())
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
    private List<DatFile> loadFiles(List<DatFile> filesToLoad) {
        // TODO clean this mess up
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
            Util.printException(Main.awtRoot, e, true);
            return null;
        }

        if (progressDialog.isDisplayable()) {
            progressDialog.dispose();
        }

        final Map<DatFile, Throwable> mapNotLoaded = threads.stream()
                .filter(LoadThread::isFailed)
                .collect(Collectors.toMap(LoadThread::getDatFile, LoadThread::getError));

        if (!mapNotLoaded.isEmpty()) {
            showLoadError(mapNotLoaded);
            return null;
        }

        List<DatFile> filesLoaded = threads.stream()
                .filter(LoadThread::isCompleted)
                .map(LoadThread::getDatFile)
                .toList();

        if (!filesLoaded.isEmpty()) {
            DatFile.LOADED.addAll(filesLoaded);
            DatFile.LOADED.forEach(DatFile::buildLinks);
            DatFile.LOADED.forEach(df -> df.dummyEntryGroup.sort(null));

            return DatFile.LOADED.stream().sorted().toList();
        } else {
            return null;
        }
    }

    private void showLoadError(Map<DatFile, Throwable> notLoaded) {
        var errorMessage = new Alert(Alert.AlertType.ERROR);
        errorMessage.getDialogPane().setMinSize(400, Region.USE_PREF_SIZE);
        Util.setAlertIcon(errorMessage);
        errorMessage.setTitle("ERROR");
        errorMessage.setHeaderText("An error occurred during the loading of DAT files!");

        var coreMessage = new StringBuilder(
                """
                Make sure that you:

                - unpacked and decompressed the data.ssa correctly
                - use files supported by DB Editor
                - selected the correct folder
                - selected the correct game version (EEC/AOC)

                You can use EE Studio II for unpacking SSA files.
                ------------------------------------------------
                Following files failed to load:
                """);

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
            Main.exit();
        }
    }

}
