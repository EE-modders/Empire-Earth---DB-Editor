package EEmodders.database;

import EEmodders.Main;
import EEmodders.Utils.Util;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.yaml.snakeyaml.Yaml;

import javax.naming.NameNotFoundException;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class DBMapping {
    private static final Map<String, String> DEBUG_MAPPING = Map.ofEntries(
            //Map.entry("AIUnitTargeting", "dbaiunittargeting.dat"),
            //Map.entry("Ambient Sounds", "dbambientsounds.dat")
            //Map.entry("Animals", "dbanimals.dat")
            //Map.entry("Area Effect Table", "dbareaeffect.dat")
            //Map.entry("Buttons", "dbbuttons.dat")
            //Map.entry("Calamity", "dbcalamity.dat")
            //Map.entry("Civilization", "dbcivilization.dat")
            Map.entry("Cliff Terrain", "dbcliffterrain.dat")
    );

    private static final Map<String, String> EE_MAPPING = Map.ofEntries(
            //Map.entry("AI Behavior", "aibehavior.dat"),
            Map.entry("AIUnitTargeting", "aiunittargeting.dat"),
            Map.entry("Ambient Sounds", "ambientsounds.dat"),
            Map.entry("Animals", "animals.dat"),
            Map.entry("Area Effect Table", "areaeffect.dat"),
            Map.entry("Buttons", "buttonsdata.dat"),
            Map.entry("Calamity", "calamity.dat"),
            Map.entry("Civilization", "civilization.dat"),
            Map.entry("Cliff Terrain", "cliff.dat"),

            Map.entry("ColorTable", "colortabledata.dat"),
            Map.entry("Control Events", "uicontrolevents.dat"),
            Map.entry("Effect", "effects.dat"),
            Map.entry("Event", "events.dat"),
            Map.entry("Event Details", "eventdetails.dat"),
            Map.entry("Fonts", "fonts.dat"),
            Map.entry("Form Events", "uiformevents.dat"),
            Map.entry("Game Variant", "gamevariant.dat"),
            Map.entry("Gfx Effects", "gfxeffects.dat"),
            Map.entry("Graphics", "graphicsdata.dat"),
            Map.entry("Hot Key", "hotkey.dat"),
            Map.entry("Music", "music.dat"),
            Map.entry("Objects", "objectdb.dat"),
            Map.entry("Premade Civilizations", "premadecivs.dat"),
            Map.entry("QLookUpDataExporter", "lookupdata.dat"),
            Map.entry("Random Map", "randommap.dat"),
            Map.entry("Sounds", "soundsdata.dat"),
            Map.entry("Starting Resources", "startresource.dat"),
            Map.entry("TechTree", "techtree.dat"),
            Map.entry("Terrain", "terrain.dat"),
            Map.entry("Terrain Color Master", "terraintype.dat"),
            Map.entry("Terrain Textures", "terraingraytextures.dat"),
            Map.entry("To Hit Table", "lookupfamily.dat"),
            Map.entry("UI Background", "uiback.dat"),
            Map.entry("UIControls", "uicontrols.dat"),
            Map.entry("UIForms", "uiforms.dat"),
            Map.entry("Unit Behavior", "unitbehavior.dat"),
            Map.entry("Unit Set Table", "unitset.dat"),
            Map.entry("Upgrades", "upgrade.dat"),
            Map.entry("Weapon To Hit table", "weapontohit.dat"),
            Map.entry("World", "world.dat")
    );

    public static List<DBTable> loadDatabase(DBType dbType, File dbFolder) {
        // TODO maybe add progress bar?
        try {
            DBMapping map = loadDBStructure(dbType);

            Map<Boolean, List<DBTable>> dbTables = DEBUG_MAPPING.entrySet().stream()
                    .map(entry -> new DBTable(entry.getKey(), entry.getValue(), dbFolder, map))
                    .peek(DBTable::load)
                    .filter(DBTable::isLoaded)
                    .collect(Collectors.partitioningBy(DBTable::hasFailed));

            var failedToLoad = dbTables.get(true);
            if (failedToLoad != null && !failedToLoad.isEmpty()) {
                showLoadError(failedToLoad);
            }

            return dbTables.get(false);

        } catch (Exception e) {
            // TODO replace with exception from JavaFX
            Util.printException(Main.awtRoot, e, true);
            return null;
        }
    }

    private static DBMapping loadDBStructure(DBType dbType) {
        final String fname = switch (dbType) {
            case EEC -> "EE.yml";
            case AOC -> "AOC.yml";
            case DOMW -> "DOMW.yml";
            case COTN -> "COTN.yml";
        };

        var inStream = new BufferedInputStream(Objects.requireNonNull(DBTable.class.getResourceAsStream(fname)));
        var yaml = new Yaml();

        Map<String, List<String>> dbStructures = yaml.load(inStream);
        return new DBMapping(dbStructures, fname);
    }

    private static void showLoadError(List<DBTable> notLoaded) {
        var errorMessage = new Alert(Alert.AlertType.ERROR);
        errorMessage.getDialogPane().setMinSize(400, Region.USE_PREF_SIZE);
        errorMessage.setTitle("Table Load Error");
        errorMessage.setHeaderText("An error occurred during the loading of DB tables!");
        Util.setAlertIcon(errorMessage);

        var coreMessage = new StringBuilder(
                """
                Make sure that you:

                - unpacked and decompressed the data.ssa correctly
                - selected the correct folder
                - selected the correct game version
                - use files supported by DB Editor

                You can use EE Studio II for unpacking SSA files.
                -----------------------------------------------------------------------------------------
                Following tables failed to load:
                """);

        for (var table : notLoaded) {
            coreMessage.append("- ")
                    .append(table.getName())
                    .append(" (").append(table.getFilename()).append("): ")
                    .append(table.getLoadException().getClass().getSimpleName())
                    .append("\n");
        }

        errorMessage.setContentText(coreMessage.toString());

        var btnEES = new ButtonType("EE Studio II");
        var btnTrace = new ButtonType("Stacktraces", ButtonBar.ButtonData.LEFT);
        var btnClose = new ButtonType("Close", ButtonBar.ButtonData.NEXT_FORWARD);
        var btnExit = new ButtonType("Exit", ButtonBar.ButtonData.LEFT);

        errorMessage.getButtonTypes().setAll(btnExit, btnTrace, btnEES, btnClose);

        var result = errorMessage.showAndWait();
        result.ifPresent(choice -> {
            if (choice == btnEES)
                SwingUtilities.invokeLater(Util::openEESUrl);
            else if (choice == btnExit)
                Main.exit();
        });
    }

    private final Map<String, List<String>> dbStructures;
    private final String dbStructureFilename;

    public DBMapping(Map<String, List<String>> dbStructures, String filename) {
        this.dbStructures = dbStructures;
        this.dbStructureFilename = filename;
    }

    public List<String> getDBStructure(String filename) throws NameNotFoundException {
        if (dbStructures == null)
            throw new RuntimeException("DB structure is not loaded");

        var struct = dbStructures.get(filename);
        if (struct == null) {
            throw new NameNotFoundException(
                    "%s key structure could not be found in %s".formatted(filename, dbStructureFilename));
        } else {
            return struct;
        }
    }

    public String getFilename() {
        return dbStructureFilename;
    }
}
