package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;

public class RootLayout {

    private MainApp mainApp;

    @FXML
    private VBox headerContainer;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        String home = System.getProperty("user.home");
        File masDir = new File(home + "/.mas/bg_common.png");
        mainApp.setMas(masDir.exists() && masDir.isFile());
        if (masDir.exists() && masDir.isFile()) {
            BackgroundImage BI = new BackgroundImage(new Image("file://" + home + "/.mas/bg_common.png", mainApp.primaryStage.getWidth(), headerContainer.getHeight(), false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            headerContainer.setBackground(new Background(BI));
        } else {
            BackgroundImage BI = new BackgroundImage(new Image(String.valueOf(this.getClass().getResource("default_bg.png")), mainApp.primaryStage.getWidth(), headerContainer.getHeight(), false, true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            headerContainer.setBackground(new Background(BI));
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Tegemist pole Markuse arvutiga");
            alert.setContentText("Programmi funktsionaalsus on piiratud");
            alert.showAndWait();
        }
    }
    @FXML
    private void initialize() {
    }
}
