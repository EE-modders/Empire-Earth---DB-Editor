package EEmodders.gui.scenes;

import EEmodders.Main;
import EEmodders.Utils.Util;
import EEmodders.database.DBMapping;
import EEmodders.database.DBTable;
import EEmodders.database.DBType;
import EEmodders.datmanager.Settings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// maybe useful for later: https://www.tutorialspoint.com/javafx-label-setlabelfor-method-example

public class MainWindowController {
    private Stage mainWindowStage;

    @FXML private Label versionLabel;
    @FXML private Label iconCreditLabel;
    @FXML private Label dbmodeLabel;

    @FXML private Button loadEEC;
    @FXML private Button loadAOC;
    @FXML private Button loadDOMW;
    @FXML private Button loadCOTN;

    @FXML private TextField dbListFilter;
    @FXML private VBox dbButtonList;

    @FXML private TabPane mainTabs;

    private void selectFolder(DBType dbMode) {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select folder containing .dat files for "+dbMode);

        File datDir = dirChooser.showDialog(mainWindowStage);
        if (datDir != null && datDir.isDirectory()) {
            dbListFilter.clear();
            dbListFilter.setDisable(true);
            dbButtonList.getChildren().clear();
            clearTabs();

            List<DBTable> loadedTables = DBMapping.loadDatabase(dbMode, datDir);
            if (loadedTables != null && !loadedTables.isEmpty()) {
                setDBButtons(loadedTables);
                mainWindowStage.setTitle("%s (%s)".formatted(Settings.NAME, dbMode));
                dbmodeLabel.setText(dbMode.name());
                dbListFilter.setDisable(false);
            }
        }
    }

    private void setDBButtons(List<DBTable> dbTables) {
        for (var table : dbTables) {
            var btn = new Button(table.getName());
            btn.setPrefSize(150, 25);
            btn.setAlignment(Pos.CENTER);
            btn.setTextAlignment(TextAlignment.CENTER);

            btn.setOnAction(actionEvent -> {
                String tabName = table.getName();

                // check if already opened
                for (var ltab : mainTabs.getTabs()) {
                    if (ltab.getText().equals(tabName)) {
                        mainTabs.getSelectionModel().select(ltab);
                        return;
                    }
                }

                var tab = new Tab(tabName);
                tab.setClosable(true);

                //var tabContent = generateTabContent(table);
                var tabContent = DataFrameController.loadDataFrame(table, 5);

                /*
                var anchor = new AnchorPane(tabContent);
                AnchorPane.setTopAnchor(tabContent, 0d);
                AnchorPane.setBottomAnchor(tabContent, 0d);
                AnchorPane.setLeftAnchor(tabContent, 0d);
                AnchorPane.setRightAnchor(tabContent, 0d);
                 */

                tab.setContent(tabContent);
                mainTabs.getTabs().add(tab);
                mainTabs.getSelectionModel().select(tab);
            });

            dbButtonList.getChildren().add(btn);
        }
    }

    private Node generateTabContent(DBTable dbTable) {
        final int tableWidth = 5;

        var grid = new GridPane();
        grid.setGridLinesVisible(true);

        var values = dbTable.getDBRows().get(0).getDBValues();
        for (int i = 0; i < values.size(); i++) {
            var val = values.get(i);
            var c = new Label("%s: %s".formatted(val.getName(), val.getValue()));

            grid.add(c, i%tableWidth, i/tableWidth);
            GridPane.setHgrow(c, Priority.ALWAYS);
            GridPane.setVgrow(c, Priority.ALWAYS);
        }

        return grid;
    }

    /**
     * Clears all tabs except the default home
     */
    private void clearTabs() {
        mainTabs.getTabs().stream()
                .filter(tab -> tab.getText().equals("Home"))
                .findFirst()
                .ifPresent(tab -> {
                    mainTabs.getTabs().clear();
                    mainTabs.getTabs().add(tab);
                    mainTabs.getSelectionModel().select(tab);
                });
    }

    @FXML
    private void filterDBList() {
        final var search = dbListFilter.getText().toLowerCase();
        if (search.isBlank()) {
            var sortedBtns = dbButtonList.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .map(node -> (Button) node)
                    .peek(button -> button.setVisible(true))
                    .sorted((o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()))
                    .toList();

            dbButtonList.getChildren().clear();
            dbButtonList.getChildren().addAll(sortedBtns);
            return;
        }

        // FIXME use Collectors.partitioningBy
        List<Button> visible = new ArrayList<>();
        List<Button> invisible = new ArrayList<>();

        dbButtonList.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .peek(button -> button.setVisible(FuzzySearch.partialRatio(button.getText().toLowerCase(), search) >= 75))
                .forEachOrdered(button -> {
                    if (button.isVisible())
                        visible.add(button);
                    else
                        invisible.add(button);
                });

        dbButtonList.getChildren().clear();
        dbButtonList.getChildren().addAll(visible);
        dbButtonList.getChildren().addAll(invisible);
    }

    @FXML
    private void showAbout() {
        Main.showAbout();
    }

    @FXML
    private void exit() {
        Main.exit();
    }

    public void initUI(Stage stage) {
        mainWindowStage = stage;

        mainWindowStage.setTitle(Settings.NAME);
        mainWindowStage.getIcons().add(Util.getDBEditorIcon());

        versionLabel.setText(Settings.VERSION);
        iconCreditLabel.setText(Settings.DB_ICON_CREDIT);
        dbmodeLabel.setText("---");

        loadEEC.setOnAction(ev -> selectFolder(DBType.EEC));
        loadAOC.setOnAction(ev -> selectFolder(DBType.AOC));
    }
}
