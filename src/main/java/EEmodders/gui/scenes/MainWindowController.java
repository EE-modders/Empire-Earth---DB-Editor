package EEmodders.gui.scenes;

import EEmodders.Core;
import EEmodders.Main;
import EEmodders.datmanager.DatFile;
import EEmodders.datmanager.Settings;
import EEmodders.datmanager.Util;
import EEmodders.gui.components.JButtonRed;
import EEmodders.gui.components.JPanelEntry;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// maybe useful for later: https://www.tutorialspoint.com/javafx-label-setlabelfor-method-example

public class MainWindowController {
    private Stage mainWindowStage;
    private String dbMode;

    @FXML private Label versionLabel;
    @FXML private Label iconCreditLabel;
    @FXML private Label dbmodeLabel;
    @FXML private Button folderSelectButton;

    @FXML private TextField dbListFilter;
    @FXML private VBox dbButtonList;

    @FXML private TabPane mainTabs;

    @FXML
    private void selectFolder() {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select folder containing .dat files for "+dbMode);

        File datDir = dirChooser.showDialog(mainWindowStage);
        if (datDir != null && datDir.isDirectory()) {
            var loadedFiles = Core.getInstance().loadFiles(datDir);
            if (loadedFiles != null && !loadedFiles.isEmpty()) {
                setDBButtons(loadedFiles);
                dbListFilter.setDisable(false);
            }
        }
    }

    private void setDBButtons(List<DatFile> fileList) {
        for (var file : fileList) {
            var btn = new Button(file.getPrettyName());
            btn.setTextAlignment(TextAlignment.CENTER);
            btn.setAlignment(Pos.CENTER);
            btn.setPrefSize(150, 25);

            btn.setOnAction(actionEvent -> {
                String tabName = file.getPrettyName();

                // check if already opened
                for (var ltab : mainTabs.getTabs()) {
                    if (ltab.getText().equals(tabName)) {
                        mainTabs.getSelectionModel().select(ltab);

                        ltab.getContent().getParent().requestFocus();
                        //ltab.getContent().requestFocus();
                        return;
                    }
                }

                var tab = new Tab(tabName);
                tab.setClosable(true);

                var swingNode = new SwingNode();
                var tabPane = new AnchorPane(swingNode);

                AnchorPane.setTopAnchor(swingNode, 0d);
                AnchorPane.setBottomAnchor(swingNode, 0d);
                AnchorPane.setLeftAnchor(swingNode, 0d);
                AnchorPane.setRightAnchor(swingNode, 0d);

                var panel = new JPanel(new GridLayout(3, 1));
                panel.setFocusable(false);

                var jtf = new JTextField("NO 1", 20);
                panel.add(jtf);
                panel.add(new JTextField("NO 2", 20));
                panel.add(new JButton("NO 3"));

                swingNode.setContent(panel);
                //swingNode.getParent().requestFocus();
                swingNode.requestFocus();
                tab.setContent(tabPane);

                /*
                var newScene = new Scene(rootPane, 800, 600);
                var newStage = new Stage();
                newStage.setTitle("LOL LMAO");
                newStage.setScene(newScene);
                newStage.show();
                 */

                //testSwing.setContent(panel);
                //tab.setContent(testSwing);

                /*
                var frame = file.openInEditor(swingNode.getContent(), true);
                swingNode.setContent(frame.getRootPane());
                 */

                //tab.setContent(tabPane);
                mainTabs.getTabs().add(tab);
                mainTabs.getSelectionModel().select(tab);
            });

            dbButtonList.getChildren().add(btn);
        }
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

    public void initUI(Stage stage, boolean isAOC) {
        this.mainWindowStage = stage;
        dbMode = isAOC ? "AOC" : "EEC";

        mainWindowStage.setTitle(Settings.NAME+" ("+dbMode+")");
        mainWindowStage.getIcons().add(Util.getDBEditorIcon());

        versionLabel.setText(Settings.VERSION);
        iconCreditLabel.setText(Settings.DB_ICON_CREDIT);
        dbmodeLabel.setText(dbMode);

        folderSelectButton.setText("Select DAT folder for "+dbMode);
    }
}
