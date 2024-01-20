package ee.mas.fdpanel;

import javafx.fxml.FXML;
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
        if (masDir.exists() && masDir.isFile() && mainApp.platform.equals("VERIFIED")) {
            BackgroundImage BI = new BackgroundImage(new Image("file://" + home + "/.mas/bg_common.png", mainApp.primaryStage.getWidth(), headerContainer.getHeight(), false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            headerContainer.setBackground(new Background(BI));
        } else {
            BackgroundImage BI = new BackgroundImage(new Image(String.valueOf(this.getClass().getResource("default_bg.png")), mainApp.primaryStage.getWidth(), headerContainer.getHeight(), false, true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            headerContainer.setBackground(new Background(BI));
        }
    }
    @FXML
    private void initialize() {
    }
}
