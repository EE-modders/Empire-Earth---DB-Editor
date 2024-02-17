package EEmodders.gui.scenes;

import EEmodders.database.DBRow;
import EEmodders.database.DBTable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DataFrameController {

    @FXML private ListView<DBRow> dbRowListView;
    @FXML private SplitPane splitPane;

    public void loadTable(DBTable dbTable, int tableWidth) {
        dbRowListView.getItems().addAll(dbTable.getDBRows());
        dbRowListView.getSelectionModel().selectedItemProperty().addListener((val, o, n) -> {
            loadRow(n, tableWidth);
        });
        dbRowListView.getSelectionModel().selectFirst();
    }

    private void loadRow(DBRow dbRow, int tableWidth) {
        var dbValueGrid = new GridPane();
        dbValueGrid.getChildren().clear();

        var values = dbRow.getDBValues();
        for (int i = 0; i < values.size(); i++) {
            var value = values.get(i);

            var input = new TextField(value.getValue().toString());
            var label = new Label("(%s) %s".formatted(i, value.getName()));
            label.setLabelFor(input);

            var layout = new VBox(label, input);
            layout.setSpacing(5d);

            dbValueGrid.add(layout, i%tableWidth, i/tableWidth);
            GridPane.setHgrow(layout, Priority.ALWAYS);
        }
        // TODO add change listener

        dbValueGrid.setHgap(10d);
        dbValueGrid.setVgap(10d);
        dbValueGrid.setGridLinesVisible(false);
        dbValueGrid.setPadding(new Insets(5d));

        if (splitPane.getItems().size() < 2) {
            splitPane.getItems().add(dbValueGrid);
            splitPane.setDividerPosition(0, 0.2d);
        } else {
            splitPane.getItems().set(1, dbValueGrid);
        }
    }

    public static Node loadDataFrame(DBTable dbTable, int tableWidth) {
        try {
            var fxmlloader = new FXMLLoader(DataFrameController.class.getResource("DataFrame.fxml"));
            Node content = fxmlloader.load();

            DataFrameController controller = fxmlloader.getController();
            controller.loadTable(dbTable, tableWidth);

            return content;
        } catch (IOException e) {
            return new Label(e.getMessage());
        }
    }
}
