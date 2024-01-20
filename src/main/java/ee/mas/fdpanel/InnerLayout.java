package ee.mas.fdpanel;

import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.*;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.parser.RtfStringSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InnerLayout {

    private MainApp mainApp;

    private String home = System.getProperty("user.home");

    private FlashDrive fd;

    @FXML
    private CheckBox autorunCheck;

    @FXML
    private Label editionLabel;

    @FXML
    private Label capacityLabel;

    @FXML
    private Label filesystemLabel;

    @FXML
    private Label mountLabel;

    @FXML
    private Label deviceLabel;

    @FXML
    private CheckBox fullscreenCheck;

    @FXML
    private CheckBox choosedevCheck;

    @FXML
    private ChoiceBox usersComboBox;

    @FXML
    private TextFlow rtfDisplay;

    @FXML
    private Label newsCounterLabel;

    @FXML
    private ListView<String> videoHighlights;

    @FXML
    private Button playButton;

    @FXML
    private ListView<String> quickApps;

    @FXML
    private ImageView qAppThumbnail;

    @FXML
    private Label qAppName;

    @FXML
    private Label qAppDescription;

    @FXML
    private Button qAppOpen;

    @FXML
    private Button qAppManage;

    @FXML
    private AnchorPane thumbnailParentNode;

    @FXML
    private Label versionLabel;

    @FXML
    private PieChart statChart;

    public final ObservableList<String> videos = FXCollections.observableArrayList();

    public final ObservableList<String> quick_apps = FXCollections.observableArrayList();

    private int newsIdx = 1;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setFd(FlashDrive fd) {
        this.fd = fd;
    }
    @FXML
    private void initialize() throws IOException {
        FileInputStream fstream = new FileInputStream(this.home + "/.mas/settings2.sf");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine = br.readLine();
        //Close the input stream
        fstream.close();
        autorunCheck.selectedProperty().set(strLine.contains("AutoRun=true"));
    }

    public void GatherInfo() throws IOException {

        editionLabel.setText(fd.GetEdition());
        mountLabel.setText(fd.GetMount());
        capacityLabel.setText(fd.GetDiskSize());
        filesystemLabel.setText(fd.GetFilesystem());
        deviceLabel.setText(fd.GetDevice());

        choosedevCheck.setDisable(mainApp.drives.size() < 2);
        usersComboBox.setItems(FXCollections.observableList(fd.GetUsers()));
        usersComboBox.getSelectionModel().select(0);
        this.newsIdx = 1;

        this.quick_apps.clear();
        this.videos.clear();
        this.videos.addAll(this.fd.GetVideos());
        this.quick_apps.addAll(this.fd.GetQApps());
        videoHighlights.setItems(this.videos);
        quickApps.setItems(this.quick_apps);

        playButton.setDisable(true);
        LoadNews(newsIdx);
        versionLabel.setText(String.format("Versioon %s", mainApp.version));

        long batch_size = fd.CalcDirSize("/Pakkfailid");
        long mas_size = fd.CalcDirSize("/markuse asjad/markuse asjad");
        long os_size = fd.CalcDirSize("/multiboot");
        long QApps_size = fd.CalcDirSize("/markuse asjad/Kiirrakendused");
        long ps2_size = fd.CalcDirSize("/POPS") + fd.CalcDirSize("/DVD") + fd.CalcDirSize("/CD");
        long misc_size = fd.GetOccupiedSpace() - batch_size - mas_size - os_size - QApps_size - ps2_size;
        long free_space = fd.GetFreeSpace();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("markuse asjad (" + fd.GetNiceSize(mas_size) + ")", mas_size),
                new PieChart.Data("Pakkfailid (" + fd.GetNiceSize(batch_size) + ")", batch_size),
                new PieChart.Data("Operatsioonsüsteemid (" + fd.GetNiceSize(os_size) + ")", os_size),
                new PieChart.Data("Kiirrakendused (" + fd.GetNiceSize(QApps_size) + ")", QApps_size),
                new PieChart.Data("PS2 mängud (" + fd.GetNiceSize(ps2_size) + ")", ps2_size),
                new PieChart.Data("Muud asjad (" + fd.GetNiceSize(misc_size) + ")", misc_size),
                new PieChart.Data("Vaba ruum (" + fd.GetNiceSize(free_space) + ")", free_space));
        statChart.setTitle("Ruumi kasutuse statistika");
        statChart.setData(pieChartData);
        pieChartData.getLast().getNode().setStyle("-fx-pie-color: #fff0;");
    }

    private void LoadNews(int idx) throws IOException {
        newsCounterLabel.setText("Uudis " + idx + "/5");
        rtfDisplay.getChildren().clear();
        InputStream is = new FileInputStream(fd.GetMount() + "/E_INFO/uudis" + idx + ".rtf");
        StringTextConverter converter = new StringTextConverter();
        converter.convert(new RtfStreamSource(is));
        String extractedText = converter.getText();
        for (String line: extractedText.split("\n")) {
            Text t =  new Text(line + "\n");
            rtfDisplay.getChildren().add(t);
        }
    }

    @FXML
    private void refreshTopic() {
        try {
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
    private void reloadDevices() throws InterruptedException {
        try {
            mainApp.ReloadDevices(choosedevCheck.isSelected());
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

    @FXML
    private void toggleAutorun() {
        try {
            String outvalue = autorunCheck.selectedProperty().get() ? "true" : "false";
            PrintStream printStr = new PrintStream(new File(this.home + "/.mas/settings2.sf"));
            Runtime.getRuntime().addShutdownHook(new Thread(printStr::close));
            printStr.println("AutoRun=" + outvalue);
            printStr.flush();
        } catch (IOException ioe) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Seadistuse muutmine");
            alert.setHeaderText("Faili kirjutamine nurjus");
            alert.setContentText(ioe.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void toggleFullScreen() {
        Boolean b = fullscreenCheck.selectedProperty().get();
        mainApp.setFullscreen(b);
    }

    private void openUsrFolder(String subdir) {
        try {
            Runtime r = Runtime.getRuntime();
            if (usersComboBox.getSelectionModel().getSelectedItem() != null) {
                r.exec("xdg-open file://" + fd.GetMount() + "/markuse%20asjad/markuse%20asjad/" + usersComboBox.getSelectionModel().getSelectedItem().toString().replace(" ", "%20") + "/" + subdir.replace(" ", "%20"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onAllClicked() { openUsrFolder(""); }

    @FXML
    private void onHelpClicked() { openUsrFolder("Abi"); }

    @FXML
    private void onVideosClicked() { openUsrFolder("Minu videod"); }

    @FXML
    private void onOtherClicked() { openUsrFolder("Muud asjad"); }

    @FXML
    private void onMusicClicked() { openUsrFolder("Muusika"); }

    @FXML
    private void onInstallableClicked() { openUsrFolder("Paigaldatavad failid"); }

    @FXML
    private void onPicsClicked() { openUsrFolder("Pildid"); }

    @FXML
    private void onPowerPointClicked() { openUsrFolder("PowerPoint"); }

    @FXML
    private void onPptTempsClicked() { openUsrFolder("PowerPointi mallid"); }

    @FXML
    private void onTextClicked() { openUsrFolder("Tekst"); }

    @FXML
    private void onWordClicked() { openUsrFolder("Word"); }

    private void openGlobalFolder(String subdir) {
        try {
            Runtime r = Runtime.getRuntime();
            r.exec("xdg-open file://" + fd.GetMount().replace(" ", "%20") + "/" + subdir.replace(" ", "%20"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile(String filename, String... pref) {
        try {
            String prefix = pref.length > 0 ? pref[0] : "xdg-open";
            Runtime r = Runtime.getRuntime();
            String cmd = prefix + " file://" + fd.GetMount().replace(" ", "%20") + filename.replace(" ", "%20");
            System.out.println(cmd);
            r.exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRootClicked() {openGlobalFolder("");}
    @FXML
    private void onHelpCenterClicked() {openGlobalFolder("markuse asjad/Abi ja info");}
    @FXML
    private void onMasClicked() {openGlobalFolder("markuse asjad");}
    @FXML
    private void onQAppsClicked() {openGlobalFolder("markuse asjad/Kiirrakendused");}
    @FXML
    private void onUsersClicked() {openGlobalFolder("markuse asjad/markuse asjad");}
    @FXML
    private void onLatestVidsClicked() {openGlobalFolder("Markuse_videod");}
    @FXML
    private void onBatchClicked() {openGlobalFolder("Pakkfailid");}
    @FXML
    private void onOSClicked() {openGlobalFolder("multiboot");}
    @FXML
    private void onGoClicked() {openGlobalFolder("markuse asjad/markuse asjad/Mine");}

    @FXML
    private void enableDisablePlayButton() {
        playButton.setDisable(videoHighlights.getSelectionModel().getSelectedIndices().isEmpty());
    }

    @FXML
    private void playVideo() {
        String fileName = videoHighlights.getSelectionModel().getSelectedItem();
        openFile("/Markuse_videod/" + fileName);
    }

    @FXML
    private void onChangeQAppSelection() {
        if (!quickApps.getSelectionModel().getSelectedIndices().isEmpty()) {
            qAppName.setText(quickApps.getSelectionModel().getSelectedItem());
            String uri = "file://" + (fd.GetMount() + "/markuse asjad/Kiirrakendused/" + qAppName.getText() + "/" + qAppName.getText() + "ScreenShot.bmp").replaceAll(" ", "%20");
            Image image = new Image(uri);
            qAppThumbnail.setImage(image);
            qAppThumbnail.fitWidthProperty().bind(thumbnailParentNode.widthProperty());
            qAppDescription.setText(fd.GetQAppDescription(qAppName.getText()));
            Boolean requiresWine = fd.DoesQAppRequireWine(qAppName.getText());
            if (requiresWine) {
                qAppName.setText(qAppName.getText() + " (Wine)");
            }
        }
        qAppOpen.setDisable(quickApps.getSelectionModel().getSelectedIndices().isEmpty());
    }

    @FXML
    private void onQAppOpenClicked() throws IOException {
        String appName = qAppName.getText().replace(" (Wine)", "");
        String uri = "";
        boolean wine = !appName.equals(qAppName.getText());
        if (wine) {
            uri = "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Portable.exe";
            openFile(uri, "wine");
        } else {
            uri = "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Portable.AppImage";
            Runtime r = Runtime.getRuntime();
            String cmd = "sh -c " + " /" + fd.GetMount() + uri;
            r.exec(cmd);
        }
    }
}
