package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class ColorChooser {
    private MainApp mainApp;
    private Stage dialogStage;
    public boolean DialogResultOK = false;

    @FXML
    private ColorPicker colorValue;

    @FXML
    private AnchorPane anchorPane;

    public void setMainApp(MainApp m) {this.mainApp = m;}
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    @FXML
    public void initialize() {
        InitTheme();
    }

    private void InitTheme() {
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }
    @FXML
    private void okClicked() {
        this.DialogResultOK = true;
        this.dialogStage.close();
    }

    @FXML
    private void cancelClicked() {
        this.DialogResultOK = false;
        this.dialogStage.close();
    }

    public Color GetColor() {
        return colorValue.getValue();
    }

    public void SetColor(Color rgb) {
        colorValue.setValue(rgb);
    }
}
