package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class BackupManager {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label backupLocLabel;

    private MainApp mainApp;

    private Stage ds;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.ds = dialogStage;
    }

    @FXML
    public void initialize() throws IOException {
        InitTheme();
        if (!Files.exists(Path.of(System.getProperty("user.home") + "/.fdpanel_backups"))) {
            ButtonType yes = new ButtonType("Jah", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("Ei", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selleks, et luua varukoopiaid tuleb kõigepealt luua kataloog kasutaja kodukausta, kuhu varukoopiad talletatakse. Selleks on vaja luua kaust \"" + System.getProperty("user.home") + "/.fdpanel_backups\". Kas soovite jätkata?", no, yes);
            alert.setTitle("Esimene käivitus");
            alert.setHeaderText("Varundushaldur");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(yes) == no) {
                ds.close();
            }
            Files.createDirectory(Path.of(System.getProperty("user.home") + "/.fdpanel_backups"));
        }
        backupLocLabel.setText(System.getProperty("user.home") + "/.fdpanel_backups");

    }

    private void InitTheme() {
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }
}
