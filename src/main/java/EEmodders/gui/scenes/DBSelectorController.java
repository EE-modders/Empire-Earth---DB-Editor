package EEmodders.gui.scenes;

import EEmodders.datmanager.Core;
import EEmodders.datmanager.DatFile;
import EEmodders.gui.MainFrame;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;

public class DBSelectorController {

    @FXML private Button loadDBButton;
    @FXML private BorderPane borderPane;
    @FXML private Label versionLabel;
    @FXML private Label creditLabel;

    @FXML
    private void selectFolder() {
        var dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Select the folder which contains the EE .dat files");

        File binDir = dirChooser.showDialog(Core.getStage());

        if (binDir != null && binDir.isDirectory())
            MainFrame.instance.loadFiles(binDir);
    }

    @FXML
    private void showAbout() {
        Core.showAbout();
    }

    @FXML
    public void exit() {
        Core.exit();
    }

    public void setVersionLabel(String version) {
        versionLabel.setText("Version: v"+version);
    }

    public void setCreditLabel(String string) {
        creditLabel.setText(string);
    }

    public void setLoadDBButton(String string) {
        loadDBButton.setText(string);
    }

    public void setDBButtons(List<DatFile> fileList) {
        var buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < fileList.size(); i++) {
            var btn = new Button();
            var datfile = fileList.get(i);

            btn.setText(datfile.getPrettyName());
            btn.setTextAlignment(TextAlignment.CENTER);
            btn.setAlignment(Pos.CENTER);
            btn.setPrefSize(150, 40);

            var eventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    datfile.openInEditor(MainFrame.instance, false);
                }
            };
            btn.setOnMouseClicked(eventHandler);

            buttonGrid.add(btn, i%3, i/3);
        }

        borderPane.setCenter(buttonGrid);

        Core.getStage().setWidth(500);
        Core.getStage().setHeight(950);
    }
}
