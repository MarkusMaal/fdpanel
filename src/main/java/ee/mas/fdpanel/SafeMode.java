package ee.mas.fdpanel;

import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SafeMode {
    private MainApp mainApp;
    private String home = System.getProperty("user.home");
    private FlashDrive fd;
    public final ObservableList<String> videos = FXCollections.observableArrayList();

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setFd(FlashDrive fd) {
        this.fd = fd;
    }
    private int newsIdx = 1;

    @FXML
    private Label newsCounterLabel;

    @FXML
    private TextFlow rtfDisplay;

    @FXML
    private ListView<String> videoHighlights;

    @FXML
    private Button playButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void initialize() {
        anchorPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        if (new File("/tmp/fdpanel_style.css").exists()) {
            anchorPane.getStylesheets().add("file:///tmp/fdpanel_style.css");
        }
    }

    public void GatherInfo() throws IOException {
        if (mainApp.platform.isEmpty()) { return; }
        this.videos.clear();
        this.videos.addAll(this.fd.GetVideos());
        this.newsIdx = 1;
        LoadNews(newsIdx);
    }

    private void LoadNews(int idx) throws IOException {
        newsCounterLabel.setText("Uudis " + idx + "/5");
        if (mainApp.platform.isEmpty()) { return; }
        rtfDisplay.getChildren().clear();
        InputStream is = new FileInputStream(fd.GetMount() + "/E_INFO/uudis" + idx + ".rtf");
        StringTextConverter converter = new StringTextConverter();
        converter.convert(new RtfStreamSource(is));
        String extractedText = converter.getText();
        boolean isFirst = true;
        for (String line: extractedText.split("\n")) {
            Text t =  new Text(line.replace("·\t", "  · ") + "\n");
            if (isFirst) {
                t.setStyle("-fx-font-weight: bold");
            }
            t.setFill(mainApp.schemeFg);
            rtfDisplay.getChildren().add(t);
            isFirst = false;
        }
        videoHighlights.setItems(this.videos);
    }


    @FXML
    private void refreshTopic() {
        try {
            if (mainApp.platform.isEmpty()) { return; }
            LoadNews(this.newsIdx);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Uudise avamine nurjus");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void nextTopic() {
        try {
            this.newsIdx ++;
            if (this.newsIdx > 5) {
                this.newsIdx = 1;
            }
            LoadNews(this.newsIdx);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Uudise avamine nurjus");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void previousTopic() {
        try {
            this.newsIdx --;
            if (this.newsIdx < 1) {
                this.newsIdx = 5;
            }
            LoadNews(this.newsIdx);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Uudise avamine nurjus");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    private void enableDisablePlayButton() {
        playButton.setDisable(videoHighlights.getSelectionModel().getSelectedIndices().isEmpty());
    }

    @FXML
    private void playVideo() {
        String fileName = videoHighlights.getSelectionModel().getSelectedItem();
        openFile("/Markuse_videod/" + fileName);
    }


    private void openFile(String filename, String... pref) {
        try {
            if (mainApp.platform.isEmpty()) { return; }
            String prefix = pref.length > 0 ? pref[0] : "xdg-open";
            Runtime r = Runtime.getRuntime();
            String cmd = prefix + " file://" + fd.GetMount().replace(" ", "%20") + filename.replace(" ", "%20");
            r.exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void reloadDevices() throws InterruptedException {
        try {
            if (mainApp.platform.isEmpty()) { return; }
            mainApp.ReloadDevices(true);
            this.fd = mainApp.fd;
            this.GatherInfo();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Ühtegi mälupulka ei leitud");
            alert.setContentText("Sisestage ja haakige mälupulk, seejärel vajutage \"OK\", et jätkata.");
            alert.showAndWait();
            reloadDevices();
        }
    }
}
