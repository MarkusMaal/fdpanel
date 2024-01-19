package ee.mas.fdpanel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;


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
    }

    @FXML
    private void reloadDevices() {
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
}
