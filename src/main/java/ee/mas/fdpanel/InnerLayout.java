package ee.mas.fdpanel;

import com.rtfparserkit.converter.text.StringTextConverter;
import com.rtfparserkit.parser.RtfStreamSource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @FXML
    private Label vfStatusLabel;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Label gettingInfoLabel;
    @FXML
    private TabPane primaryTabPane;
    @FXML
    private Button unsecurePinButton;
    @FXML
    private ListView<String> videoHightlightsDev;
    @FXML
    private Button addVideoButton;
    @FXML
    private FlowPane newsFlowDev;
    @FXML
    private Button integrityCheckButton;
    @FXML
    private Button unlockDevButton;

    private Image initialImage;

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
        initialImage = qAppThumbnail.getImage();
        if (Files.exists(Path.of(this.home + "/.mas/settings2.sf"))) {
            FileInputStream fstream = new FileInputStream(this.home + "/.mas/settings2.sf");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            //Close the input stream
            fstream.close();
            autorunCheck.selectedProperty().set(strLine.contains("AutoRun=true"));
        } else {
            autorunCheck.selectedProperty().set(false);
            autorunCheck.setDisable(true);
        }
    }

    public void GatherInfo() throws IOException {
        videoHightlightsDev.setDisable(true);
        addVideoButton.setDisable(true);
        newsFlowDev.setDisable(true);
        integrityCheckButton.setDisable(true);
        unlockDevButton.setText("Lukusta lahti");

        primaryTabPane.setVisible(mainApp.platform.isEmpty());
        spinner.setVisible(!mainApp.platform.isEmpty());
        gettingInfoLabel.setVisible(!mainApp.platform.isEmpty());
        capacityLabel.setText(fd.GetDiskSize());
        filesystemLabel.setText(fd.GetFilesystem());
        deviceLabel.setText(fd.GetDevice());
        choosedevCheck.setDisable(mainApp.drives.size() < 2);
        usersComboBox.setItems(FXCollections.observableList(fd.GetUsers()));
        usersComboBox.getSelectionModel().select(0);
        qAppThumbnail.setImage(this.initialImage);
        qAppName.setText("Kiirrakendused");
        qAppDescription.setText("Kiirrakendused on nagu tavaliselt rakendused, kuid neil on üks suur erinevus - neid saab käivitada otse mälupulgalt ilma midagi arvutisse installimata! Klõpsake ühel kiirakendusel, et näha lisainfot.");

        if (this.fd.IsSecurePin()) {
            unsecurePinButton.setText("Ebaturvaline PIN kood");
        } else {
            unsecurePinButton.setText("Loo turvaline PIN kood");
        }

        this.newsIdx = 1;

        this.quick_apps.clear();
        this.videos.clear();

        playButton.setDisable(true);
        LoadNews(newsIdx);
        versionLabel.setText(String.format("Versioon %s", mainApp.version));
        try {
            vfStatusLabel.setText("Verifile olek: " +  (mainApp.platform.length() < 6?"B" /*ees */ +
                    "A".replace("A", "Y") /*esmen*/ + "P" /*lease*/ + "A" + /* ssess */
                    "S" + /*unny */ "S" /* mile */:new Verifile(this.home + "/.mas").MakeAttestation()));
        } catch (NoSuchAlgorithmException e) {
            vfStatusLabel.setText("Verifile olek: NO_SUCH_ALGORITHM");
        }
        if (mainApp.platform.isEmpty()) { return;}
        GetSizeTask sizeTask = new GetSizeTask(this.fd);
        PopulateArraysTask populTask = new PopulateArraysTask(this.fd);

        sizeTask.setOnRunning(event -> {
            statChart.setVisible(false);
        });

        populTask.setOnSucceeded(event -> {
            this.videos.addAll(populTask.getValue().get("Videos"));
            this.quick_apps.addAll(populTask.getValue().get("QApps"));
            videoHightlightsDev.setItems(this.videos);
            videoHighlights.setItems(this.videos);
            quickApps.setItems(this.quick_apps);
        });

        sizeTask.setOnSucceeded(event -> {
            spinner.setVisible(false);
            gettingInfoLabel.setVisible(false);
            primaryTabPane.setVisible(true);
            statChart.setVisible(true);
                HashMap<String, Long> result = sizeTask.getValue();
                long misc_size;
                long free_space;
                try {
                    misc_size = fd.GetOccupiedSpace() - result.get("Pakkfailid") - result.get("Markuse asjad") - result.get("Operatsioonsüsteemid") - result.get("Kiirrakendused") - result.get("PS2 mängud");
                    free_space = fd.GetFreeSpace();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("markuse asjad (" + fd.GetNiceSize(result.get("Markuse asjad")) + ")", result.get("Markuse asjad")),
                        new PieChart.Data("Pakkfailid (" + fd.GetNiceSize(result.get("Pakkfailid")) + ")",result.get("Pakkfailid") ),
                        new PieChart.Data("Operatsioonsüsteemid (" + fd.GetNiceSize(result.get("Operatsioonsüsteemid")) + ")", result.get("Operatsioonsüsteemid")),
                        new PieChart.Data("Kiirrakendused (" + fd.GetNiceSize(result.get("Kiirrakendused")) + ")", result.get("Kiirrakendused")),
                        new PieChart.Data("PS2 mängud (" + fd.GetNiceSize(result.get("PS2 mängud")) + ")", result.get("PS2 mängud")),
                        new PieChart.Data("Muud asjad (" + fd.GetNiceSize(misc_size) + ")", misc_size),
                        new PieChart.Data("Vaba ruum (" + fd.GetNiceSize(free_space) + ")", free_space));
                statChart.setLabelsVisible(false);
                statChart.setTitle("Ruumi kasutuse statistika");
                statChart.setLabelLineLength(0);
                statChart.setData(pieChartData);
                pieChartData.getLast().getNode().setStyle("-fx-pie-color: #fff0;");
                editionLabel.setText(fd.GetEdition());
                mountLabel.setText(fd.GetMount());
        });
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(sizeTask);
        executorService.execute(populTask);
        executorService.shutdown();
    }

    private void LoadNews(int idx) throws IOException {
        newsCounterLabel.setText("Uudis " + idx + "/5");
        if (mainApp.platform.isEmpty()) { return; }
        rtfDisplay.getChildren().clear();
        InputStream is = new FileInputStream(fd.GetMount() + "/E_INFO/uudis" + idx + ".rtf");
        StringTextConverter converter = new StringTextConverter();
        converter.convert(new RtfStreamSource(is));
        String extractedText = converter.getText();
        boolean firstLine = true;
        for (String line: extractedText.split("\n")) {
            Text t =  new Text(line.replace("·\t", "  · ") + "\n");
            t.setFill(mainApp.schemeFg);
            if (firstLine) {
                t.setStyle("-fx-font-weight: bold");
            }
            rtfDisplay.getChildren().add(t);
            firstLine = false;
        }
    }

    public void RefreshNews() {
        this.refreshTopic();
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
    private void reloadDevices() throws InterruptedException {
        try {
            if (mainApp.platform.isEmpty()) { return; }
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
            if (mainApp.platform.equals("VERIFIED")) {
                String outvalue = autorunCheck.selectedProperty().get() ? "true" : "false";
                PrintStream printStr = new PrintStream(new File(this.home + "/.mas/settings2.sf"));
                Runtime.getRuntime().addShutdownHook(new Thread(printStr::close));
                printStr.println("AutoRun=" + outvalue);
                printStr.flush();
            }
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
            if (mainApp.platform.isEmpty()) { return; }
            if (usersComboBox.getSelectionModel().getSelectedItem() != null) {
                new Verifile(this.fd.GetMount()).execute("xdg-open file://" + fd.GetMount() + "/markuse%20asjad/markuse%20asjad/" + usersComboBox.getSelectionModel().getSelectedItem().toString().replace(" ", "%20") + "/" + subdir.replace(" ", "%20"));
            }
        } catch (IOException | NoSuchAlgorithmException e) {
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
            if (mainApp.platform.isEmpty()) { return; }
            new Verifile(this.fd.GetMount()).execute("xdg-open file://" + fd.GetMount().replace(" ", "%20") + "/" + subdir.replace(" ", "%20"));
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void openFile(String filename, String... pref) {
        try {
            String prefix = pref.length > 0 ? pref[0] : "xdg-open";
            String cmd = prefix + " file://" + fd.GetMount().replace(" ", "%20") + filename.replace(" ", "%20");
            new Verifile(this.fd.GetMount()).execute(cmd);
        } catch (IOException | NoSuchAlgorithmException e) {
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
    private void onChangeQAppSelection() throws NoSuchAlgorithmException, IOException {
        if (!mainApp.platform.equals(new Verifile(System.getProperty("user.home") + "/.mas").MakeAttestation())) { return; }
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
    private void onQAppOpenClicked() throws IOException, NoSuchAlgorithmException {
        String appName = qAppName.getText().replace(" (Wine)", "");
        String uri = "";
        boolean wine = !appName.equals(qAppName.getText());
        if (!mainApp.platform.equals(new Verifile(System.getProperty("user.home") + "/.mas").MakeAttestation())) { return; }
        if (wine) {
            uri = "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Portable.exe";
            openFile(uri, "wine");
        } else {
            uri = "/markuse asjad/Kiirrakendused/" + appName + "/" + appName + "Portable.AppImage";
            String cmd = "sh -c " + " /" + fd.GetMount() + uri;
            new Verifile(this.fd.GetMount()).execute(cmd);
        }
    }

    @FXML
    private void PreviewMe() {
        mainApp.showPreview(fd.GetMount() + "/markuse asjad/Kiirrakendused/" + qAppName.getText().replace(" (Wine)", "") + "/" + qAppName.getText().replace(" (Wine)", "") + "ScreenShot.bmp");
    }

    @FXML
    private void ReAttestateVfile() {
        try {
            vfStatusLabel.setText("Verifile olek: " + new Verifile(this.home + "/.mas").MakeAttestation());
        } catch (NoSuchAlgorithmException e) {
            vfStatusLabel.setText("Verifile olek: NO_SUCH_ALGORITHM");
        } catch (IOException e) {
            vfStatusLabel.setText("Verifile olek: IO_EXCEPTION");
        }
    }

    @FXML
    private void PinTest() {
        if (unlockDevButton.getText().equals("Lukusta")) {
            unlockDevButton.setText("Lukusta lahti");
            videoHightlightsDev.setDisable(true);
            addVideoButton.setDisable(true);
            newsFlowDev.setDisable(true);
            integrityCheckButton.setDisable(true);
            return;
        }
        String enteredPin = mainApp.showPinDialog("Sisestage PIN kood kontrollimiseks");

        try {
            if (this.fd.VerifyPin(enteredPin)) {
                unlockDevButton.setText("Lukusta");
                videoHightlightsDev.setDisable(false);
                addVideoButton.setDisable(false);
                newsFlowDev.setDisable(false);
                integrityCheckButton.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mälupulga juhtpaneel");
                alert.setHeaderText("Vale PIN kood!");
                alert.showAndWait();
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Runtime error: " + e.getMessage());
        }
    }

    @FXML
    private void ReplaceVideo() {
        if (videoHightlightsDev.getSelectionModel().getSelectedIndices().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Palun valige üks video");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Vali fail mälupulgalt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Videofailid", "*.mp4", "*.mpg", "*.wmv", "*.mov", "*.avi", "*.mkv", "*.ogv"),
                new FileChooser.ExtensionFilter("Kõik failid", "*.*")
        );
        try {
            File videoFile = fileChooser.showOpenDialog(this.mainApp.primaryStage);
            this.fd.ReplaceVideo(videoHightlightsDev.getSelectionModel().getSelectedItem(), videoFile.getAbsolutePath(), usersComboBox.getSelectionModel().getSelectedItem().toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Video asendatud");
            alert.setContentText("Video asendati edukalt! Muudatuste rakendamiseks värskendame andmeid.");
            alert.showAndWait();
            reloadDevices();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Muudatusi ei tehtud");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void DisplayNewPin(String newPin) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mälupulga juhtpaneel");
        alert.setHeaderText("PIN kood muudeti edukalt");
        alert.setContentText("Uus PIN kood: " + newPin);
        alert.showAndWait();
    }

    @FXML
    private void UnsecureConversion() throws NoSuchAlgorithmException {
        boolean correctPin = this.fd.VerifyPin(mainApp.showPinDialog("Sisestage praegune PIN kood"));
        if (!correctPin) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Vale PIN kood!");
            alert.showAndWait();
            return;
        }
        ButtonType yes = new ButtonType("Jah", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Ei", ButtonBar.ButtonData.CANCEL_CLOSE);
        if (this.fd.IsSecurePin()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Kas olete kindel, et soovite välja lülitada turvalise PIN koodi ja üle minna lihttekstilisele koodile? Ebaturvaline PIN kood on vajalik ainult tagasiühilduvuseks vanemate mälupulga haldamise programmidega.", no, yes);
            alert.setTitle("Ebaturvaline PIN kood");
            alert.setHeaderText("Hoiatus");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(yes) == no) {
                return;
            }
            String newPin = mainApp.showPinDialog("Sisestage uus või sama PIN kood");
            if (mainApp.fd.ConvertInsecure(newPin)) {
                this.fd = mainApp.fd;
                DisplayNewPin(newPin);
                this.unsecurePinButton.setText("Loo turvaline PIN kood");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Kas olete kindel, et soovite välja üle minna turvalisele PIN koodile? Selle tegevuse tagajärjel ei saa seda mälupulka hallata vanemate programmidega nn mälupulga universaalprogrammiga..", no, yes);
            alert.setTitle("Loo turvaline PIN kood");
            alert.setHeaderText("Hoiatus");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(yes) == no) {
                return;
            }
            String newPin = mainApp.showPinDialog("Sisestage uus või sama PIN kood");
            if (mainApp.fd.ConvertSecure(newPin)) {
                this.fd = mainApp.fd;
                DisplayNewPin(newPin);
                this.unsecurePinButton.setText("Ebaturvaline PIN kood");
            }
        }
    }

    @FXML
    private void ChangePin() {
        // verify old PIN
        String enteredPin = mainApp.showPinDialog("Sisestage vana PIN kood");
        try {
            if (!this.fd.VerifyPin(enteredPin)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mälupulga juhtpaneel");
                alert.setHeaderText("Vale PIN kood!");
                alert.setContentText("Muudatusi ei tehtud");
                alert.showAndWait();
                return;
            }
            // generate new PIN
            String newPin = mainApp.showPinDialog("Sisestage uus PIN kood");
            if (mainApp.fd.SetPin(newPin)) {
                this.fd = mainApp.fd;
                DisplayNewPin(newPin);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mälupulga juhtpaneel");
                alert.setHeaderText("Koodi muutmine nurjus");
                alert.setContentText("Palun proovige hiljem uuesti!");
                alert.showAndWait();
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Runtime error: " + e.getMessage());
        }
    }

    @FXML
    private void BackManClicked() {
        boolean correctPin = false;
        try {
            correctPin = this.fd.VerifyPin(mainApp.showPinDialog("Sisestage praegune PIN kood"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (!correctPin) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Vale PIN kood!");
            alert.showAndWait();
            return;
        }
        mainApp.showBackupMan();
    }

    @FXML
    private void SetName() {
        boolean correctPin = false;
        try {
            correctPin = this.fd.VerifyPin(mainApp.showPinDialog("Sisestage praegune PIN kood"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (!correctPin) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Vale PIN kood!");
            alert.showAndWait();
            return;
        }
        String flashName = mainApp.showInputBox("Sisesta uus mälupulga nimi");
        this.fd.SetName(flashName);
    }

    @FXML
    private void ConvertEdition() throws IOException {
        boolean correctPin = false;
        try {
            correctPin = this.fd.VerifyPin(mainApp.showPinDialog("Sisestage praegune PIN kood"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (!correctPin) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Vale PIN kood!");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Väljaande teisendus");
        alert.setHeaderText("Programm sulgub nüüd");
        alert.setContentText("Väljaande teisendamiseks käivitame skripti ja sulgeme mälupulga programmi");
        alert.showAndWait();
        File f = new File(getClass().getResource("convert.sh").getFile());
        Files.copy(Path.of(f.getAbsolutePath()), Path.of(this.fd.GetMount() + "/E_INFO/convert.sh"), StandardCopyOption.REPLACE_EXISTING);
        Runtime rt = Runtime.getRuntime();
        rt.exec("konsole -e bash " + this.fd.GetMount() + "/E_INFO/convert.sh");
        System.exit(0);
    }

    @FXML
    private void DevNews1() { openFile("/E_INFO/uudis1.rtf"); }
    @FXML
    private void DevNews2() { openFile("/E_INFO/uudis2.rtf"); }
    @FXML
    private void DevNews3() { openFile("/E_INFO/uudis3.rtf"); }
    @FXML
    private void DevNews4() { openFile("/E_INFO/uudis4.rtf"); }
    @FXML
    private void DevNews5() { openFile("/E_INFO/uudis5.rtf"); }

    @FXML
    private void OpenExtras() {
        mainApp.showExtrasDialog();
    }

    public class GetSizeTask extends Task<HashMap<String, Long>> {
        final FlashDrive fd;

        public GetSizeTask(FlashDrive fd) {
            this.fd = fd;
        }

        @Override
        protected HashMap<String, Long> call() throws Exception {
            HashMap<String, Long> sizes = new LinkedHashMap<String, Long>();
            sizes.put("Pakkfailid", fd.CalcDirSize("/Pakkfailid") + fd.CalcDirSize("/Batch"));
            sizes.put("Markuse asjad", fd.CalcDirSize("/markuse asjad/markuse asjad"));
            sizes.put("Operatsioonsüsteemid", fd.CalcDirSize("/multiboot"));
            sizes.put("Kiirrakendused", fd.CalcDirSize("/markuse asjad/Kiirrakendused"));
            sizes.put("PS2 mängud", fd.CalcDirSize("/POPS") + fd.CalcDirSize("/DVD") + fd.CalcDirSize("/CD"));
            return sizes;
        }
    }

    public class PopulateArraysTask extends Task<HashMap<String, List<String>>> {

        final FlashDrive fd;

        public PopulateArraysTask(FlashDrive fd) {
            this.fd = fd;
        }

        @Override
        protected HashMap<String, List<String>> call() throws Exception {
            HashMap<String, List<String>> returns = new LinkedHashMap<String, List<String>>();
            returns.put("Videos", this.fd.GetVideos());
            returns.put("QApps", this.fd.GetQApps());
            return returns;
        }
    }

}
