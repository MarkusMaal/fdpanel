package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import static javafx.scene.layout.BackgroundSize.DEFAULT;
import static javafx.scene.paint.CycleMethod.REPEAT;

public class AdditionalFeatures {
    @FXML
    private ImageView previewImage;

    @FXML
    private Label functionTitle;

    @FXML
    private Label functionDesc;

    @FXML
    private VBox previewContainer;

    @FXML
    private AnchorPane anchorPane;

    private MainApp mainApp;

    public void setMainApp(MainApp m) {this.mainApp = m;}

    @FXML
    private void initialize() {
        this.ScaleImage();
        this.HoverDefault();
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }

    private void ScaleImage() {
        if (this.previewImage.getImage() == null) {return;}
        this.previewImage.setFitHeight(this.previewImage.getImage().getHeight());
        this.previewImage.setFitWidth(this.previewImage.getImage().getWidth());
        if (this.previewImage.getImage().getHeight() > this.previewContainer.getHeight()) {
            this.previewImage.setFitHeight(this.previewContainer.getHeight());
        }
        if (this.previewImage.getImage().getWidth() > this.previewContainer.getWidth()) {
            this.previewImage.setFitWidth(this.previewContainer.getWidth());
        }
    }

    @FXML
    private void HoverSafeMode() {
        Image BI = new Image(String.valueOf(this.getClass().getResource("safemode.png")));
        previewContainer.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ffffff"),null, null)));
        this.previewImage.setImage(BI);
        this.functionTitle.setText("Turvarežiim");
        this.functionDesc.setText("Käivitab mälupulga haldamise programmi minimaalses režiimis ainult uudiste lugemiseks. Programmi käivitamiseks turvarežiimis ilma tavarežiimi sisenemata, kasutage käivitamisel parameetrit --safemode.");
        this.ScaleImage();
    }

    @FXML
    private void HoverLegacyPanel() {
        Image BI = new Image(String.valueOf(this.getClass().getResource("legacypanel_1.png")));
        previewContainer.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ffffff"),null, null)));
        this.previewImage.setImage(BI);
        this.functionTitle.setText("Windowsi versioon Mälupulga juhtpaneelist");
        this.functionDesc.setText("Käivitab vanema Windowsi jaoks mõeldud versiooni mälupulga juhtpaneelist. Kasutab Wine tarkara.");
        this.ScaleImage();
    }


    @FXML
    private void HoverCustomizeTheme() {
        BackgroundImage BI = new BackgroundImage(new Image(String.valueOf(this.getClass().getResource("default_bg.png")), 508, 443, true, true), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        previewContainer.setBackground(new Background(BI));
        this.previewImage.setImage(null);
        this.functionTitle.setText("Teemade haldamine");
        this.functionDesc.setText("Juhul kui käivitasite selle programmi Markuse arvutis, saate teemat käsitsi kohandada Markuse arvuti juhtpaneelis. See valik on mõeldud olukordadeks, kus seda programmi ei kasutata Markuse arvutis.");
        this.ScaleImage();
    }

    @FXML
    private void HoverDefault() {
        Image BI = new Image(String.valueOf(this.getClass().getResource("Info.png")));
        this.previewImage.setImage(BI);
        previewContainer.setBackground(new Background(new BackgroundFill(Paint.valueOf("#00000000"),null, null)));
        this.functionTitle.setText("Lisainfo funktsiooni kohta");
        this.functionDesc.setText("Lisanduv info teatud funktsiooni kohta ilmub siia, kui viite kursori kindla nupu peale.");
        this.ScaleImage();
    }

    @FXML
    private void SafeModeClicked() {
        this.mainApp.initSafeMode();
    }

    @FXML
    private void WinVerClicked() { if (mainApp.platform.isEmpty()) { return; }
        try {
            if (mainApp.platform.isEmpty()) { return; }
            String filename = "/Markuse mälupulk.exe";
            File f = new File(mainApp.fd.GetMount() + filename);
            if (!f.exists() || f.isDirectory()) { return; }
            String prefix = "wine";
            String cmd = prefix + " file://" + mainApp.fd.GetMount().replace(" ", "%20") + filename.replace(" ", "%20");
            new Verifile(this.mainApp.fd.GetMount()).execute(cmd);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void CustomizeThemeClicked() {

    }
}
