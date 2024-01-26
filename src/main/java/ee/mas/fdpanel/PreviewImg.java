package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.net.URLEncoder;

public class PreviewImg {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label labelA;
    private Stage s;

    public void setImage(String image) {
        labelA.setBackground(null);
        BackgroundImage BI = new BackgroundImage(new Image("file://" + image, -1, -1, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);;
        labelA.setBackground(new Background(BI));
        s.setWidth(BI.getSize().getWidth());
        s.setHeight(BI.getSize().getHeight());
        s.getScene().setCursor(null);
    }

    @FXML
    private void initialize() {
        InitTheme();
    }

    public void setStage(Stage s) {
        this.s = s;
    }

    private void InitTheme() {
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }
}
