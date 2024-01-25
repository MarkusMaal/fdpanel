package ee.mas.fdpanel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriveChooser {
    private MainApp mainApp;
    private Stage dialogStage;


    public void setMainApp(MainApp m) {this.mainApp = m;}

    @FXML
    private ChoiceBox<String> drivePicker;

    @FXML
    private AnchorPane anchorPane;


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public int getSelectedDrive() {
        return drivePicker.getSelectionModel().getSelectedIndex();
    }

    public void setDrives(List<FlashDrive> drives) throws IOException {
        ArrayList<String> driveStrings = new ArrayList<>();
        for (FlashDrive drive: drives) {
            driveStrings.add(drive.GetDevice() + ": " + drive.GetDiskSize());
        }
        drivePicker.setItems(FXCollections.observableList(driveStrings));
        drivePicker.getSelectionModel().select(0);
        dialogStage.centerOnScreen();
    }
    @FXML
    private void initialize() {
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }

    @FXML
    private void okClicked() {
        dialogStage.close();
    }

    @FXML
    private void exitClicked() {
        System.exit(0);
        dialogStage.close();
    }
}
