package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

public class ThemeManager {

    private MainApp mainApp;

    public void setMainApp(MainApp m) {this.mainApp = m;}

    @FXML
    private AnchorPane anchorPane;
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
    private void LoadTheme() throws NoSuchAlgorithmException, IOException, URISyntaxException {
        mainApp.ReloadTheme();
        mainApp.primaryStage.getScene().getStylesheets().clear();
        mainApp.primaryStage.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            mainApp.primaryStage.getScene().getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
        anchorPane.getStylesheets().clear();
        mainApp.appRoot.setCustomHeader(System.getProperty("user.home") + "/.mas/bg_common.png");
        InitTheme();
    }

    @FXML
    private void RejectTheme() {
        mainApp.primaryStage.getScene().getStylesheets().clear();
        anchorPane.getStylesheets().clear();
    }

    @FXML
    private void ReplaceHeader() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vali pilt, mida kasutada päises");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Levinud pildifailid", "*.png", "*.jpg", "*.jpe", "*.jpeg", "*.bmp", "*.tiff", "*.gif"),
                new FileChooser.ExtensionFilter("Kõik failid", "*.*")
        );
        try {
            File picFile = fileChooser.showOpenDialog(this.mainApp.primaryStage);
            mainApp.appRoot.setCustomHeader(URLEncoder.encode(picFile.getAbsolutePath()).replace("%2F", "/"));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Muudatusi ei tehtud");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
