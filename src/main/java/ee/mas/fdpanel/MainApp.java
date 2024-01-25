package ee.mas.fdpanel;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {
    public Stage primaryStage;

    public RootLayout appRoot;

    public BorderPane rootLayout;

    public InnerLayout mainWindow;

    public FlashDrive fd;

    public String version = "0.4";
    public boolean isMas = false;

    public List<FlashDrive> drives = new ArrayList<>();
    private String alphabet = "abcdefghijklmnopqrstuvwxyz.,-/\\_";

    public String platform = "";

    public static boolean safeMode = false;

    public Color schemeBg;
    public Color schemeFg;
    public String style;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException, NoSuchAlgorithmException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Mälupulga juhtpaneel");
        alert.setHeaderText("Platvormi viga");

        switch (this.platform = new Verifile(System.getProperty("user.home") + this.alphabet.charAt(29) + this.alphabet.charAt(26) + "sbm".replace("m", "x").replace("s", "m").replace("b", "a").replace("x", "s")).MakeAttestation()) {
            case "TAMPERED":
                alert.setContentText("Markuse arvuti asjad pole sellesse seadmesse õigesti paigaldatud. Palun kasutage juurutamiseks sobivat tööriista. Mälupulga juhtpaneel ei tööta valesti juurutatud seadmetes.\n\nTehniline info: VFILE_TAMPERED");
                alert.showAndWait();
                System.exit(1);
                break;
            case "LEGACY":
                alert.setContentText("Markuse arvuti asjad ei ole selles seadmes juurutatud Linux-i juurutamise tööriistaga. Palun kasutage juurutamiseks sobivat tööriista. Mälupulga juhtpaneel ei tööta valesti juurutatud seadmetes.\n\nTehniline info: VFILE_LEGACY");
                alert.showAndWait();
                System.exit(2);
                break;
            case "FAILED":
                alert.setContentText("Süsteemi püsivuskontroll nurjus. Markuse mälupulga juhtpaneel ei tööta seadmetes, kus pole võimalik sooritada püsivuskontrolli.\n\nTehniline info: VFILE_FAILED");
                alert.showAndWait();
                System.exit(3);
                break;
            case "FOREIGN":
                alert.setContentText("Pole tegemist Markuse arvutiga. Programmi funktsionaalsus on piiratud.\n\nTehniline info: VFILE_FOREIGN");
                alert.showAndWait();
                break;
        }
        ReloadTheme();
        if (!this.platform.isEmpty()) {
            this.primaryStage = stage;
            this.primaryStage.setTitle("Markuse mälupulk");
        }
        findFlashDrives();
        if (!this.drives.isEmpty()) {
            int idx = 0;
            if (this.drives.size() > 1) {
                idx = showDriveChooserDialog();
            }
            if (idx == -1) { idx = 0; }
            fd = this.drives.get(idx);
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Ühtegi mälupulka ei leitud");
            alert.setContentText("Sisestage ja haakige mälupulk, seejärel vajutage \"OK\", et jätkata.");
            alert.showAndWait();
            ReloadDevices(true);
        }
        for (String par: getParameters().getRaw()) {
            if (par.equals("--safemode")) {
                safeMode = true;
            }
        }
        if (safeMode) {
            initSafeMode();
            return;
        }
        initRootLayout();
        showFirstForm();
    }

    public void ReloadDevices(boolean chooseDev) throws IOException, InterruptedException {
        this.drives.clear();
        int idx = 0;
        findFlashDrives();
        if (chooseDev && (this.drives.size() > 1)) {
            idx = showDriveChooserDialog();
        } else {
            if (this.fd != null) {
                for (int i = 0; i < this.drives.size(); i++) {
                    if (this.drives.get(i).GetDiskSize().equals(this.fd.GetDiskSize()) && this.drives.get(i).GetDevice().equals(this.fd.GetDevice())) {
                        idx = i;
                    }
                }
            } else {
                idx = 0;
            }
        }
        if (!this.drives.isEmpty()) {
            fd = this.drives.get(idx);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mälupulga juhtpaneel");
            alert.setHeaderText("Ühtegi mälupulka ei leitud");
            alert.setContentText("Sisestage ja haakige mälupulk, seejärel vajutage \"OK\", et jätkata.");
            alert.showAndWait();
            ReloadDevices(chooseDev);
        }
    }

    private void findFlashDrives() throws IOException {
        String OS = System.getProperty("os.name");
        if(OS.equals("Linux"))
        {
            StringBuilder s = new StringBuilder();
            Runtime rt = Runtime.getRuntime();
            int n=0;
            Process ps = rt.exec("ls /run/media/markus");// Write your UserName, mine is Rancho

            InputStream in = ps.getInputStream();
            while((n = in.read())!=-1)
            {
                char ch = (char) n;
                s.append(ch);
            }
            for (String drv: s.toString().split("\n")) {
                FlashDrive test = new FlashDrive("/run/media/markus/" + drv, this.isMas);
                if (test.GetValid()) {
                    this.drives.add(test);
                }
            }

        } else {
            System.out.println("This program only works with GNU/Linux");
            System.exit(0);
        }
    }

    private FXMLLoader initLayout(String resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource(resource));
        return loader;
    }

    public void initRootLayout() throws IOException {
        FXMLLoader loader = initLayout("RootLayout.fxml");
        rootLayout = (BorderPane)loader.load();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        RootLayout controller = loader.getController();
        controller.setMainApp(this);
        appRoot = controller;
        primaryStage.show();
    }

    public void showFirstForm() throws IOException {
        FXMLLoader loader = initLayout("InnerLayout.fxml");
        AnchorPane firstForm = (AnchorPane) loader.load();

        InnerLayout controller = loader.getController();
        this.mainWindow = controller;
        controller.setMainApp(this);
        controller.setFd(this.fd);
        controller.GatherInfo();
        rootLayout.setCenter(firstForm);
    }


    public static void main(String[] args) {
        launch(args);
    }

    private Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setFullscreen(Boolean b) {
        this.primaryStage.setFullScreen(b);
    }

    public String showInputBox(String topText) {
        try {
            FXMLLoader loader = new FXMLLoader();
            if (!this.platform.equals(new Verifile(System.getProperty("user.home") + "/.mas").MakeAttestation())) { return ""; }
            loader.setLocation(MainApp.class.getResource("InputBox.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle("Mälupulga juhtpaneel");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            InputBox controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setHeader(topText);
            dialogStage.showAndWait();

            return controller.getValue();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String showPinDialog(String message) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("PinEntry.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle(message);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PinEntry controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            dialogStage.showAndWait();

            return controller.getPin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int showDriveChooserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("DriveChooser.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle("Leidsime " + this.drives.size() + " mälupulka");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            DriveChooser controller = loader.getController();

            controller.setDialogStage(dialogStage);
            controller.setDrives(this.drives);
            dialogStage.showAndWait();

            return controller.getSelectedDrive();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public void showExtrasDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("AdditionalFeatures.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle("Lisa");
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AdditionalFeatures controller = loader.getController();
            controller.setMainApp(this);
            dialogStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initSafeMode() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("SafeMode.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle("Turvarežiim");
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            SafeMode controller = loader.getController();
            controller.setMainApp(this);
            controller.setFd(this.fd);
            controller.GatherInfo();
            dialogStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showThemeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ThemeManager.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.centerOnScreen();
            dialogStage.setTitle("");
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ThemeManager controller = loader.getController();
            controller.setMainApp(this);
            dialogStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void centerStage(Stage stage, double width, double height) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }

    public void setMas(boolean val) {
        this.isMas = val;
    }

    private static byte[] convertListToByteArray(List<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];

        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }

        return byteArray;
    }

    public void ReloadTheme() throws NoSuchAlgorithmException, IOException {
        String mas_root = System.getProperty("user.home") + "/.mas";
        String vf_result = new Verifile(mas_root).MakeAttestation();
        if (vf_result.equals("VERIFIED")) {
            File schemeFile = new File(mas_root + "/scheme.cfg");
            String colorData = "";
            if (!schemeFile.exists() || schemeFile.isDirectory()) {
                return;
            }
            FileInputStream fstream;
            try {
                fstream = new FileInputStream(schemeFile.getAbsoluteFile());
            } catch (FileNotFoundException ignored) { return; }
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, StandardCharsets.UTF_8));

            try {
                colorData  = br.readLine();
                fstream.close();
            } catch (IOException ignored) {}

            String[] bgs = colorData.split(";")[0].split(":");
            String[] fgs = colorData.split(";")[1].split(":");
            this.schemeBg = Color.rgb(Integer.parseInt(bgs[0]),
                    Integer.parseInt(bgs[1]),
                    Integer.parseInt(bgs[2]));
            this.schemeFg = Color.rgb(Integer.parseInt(fgs[0]),
                    Integer.parseInt(fgs[1]),
                    Integer.parseInt(fgs[2]));
            this.StyleFactory();
        }
    }

    private String GetRgbString(Color rgb) {
        return "rgb(" + (int)(255.0 * rgb.getRed()) + ", " + (int)(255.0 * rgb.getGreen()) + ", " + (int)(255.0 * rgb.getBlue()) +  ")";
    }

    private String InsertBgFg(String selector, Color bg, Color fg, boolean fxBase) {
        StringBuilder bgStr = new StringBuilder();
        if (!fxBase) {
            bgStr.append(selector).append("\n");
            bgStr.append("{").append("\n");
            bgStr.append("-fx-background-color: " + GetRgbString(bg) + ";").append("\n");
            bgStr.append("-fx-text-fill: " + GetRgbString(fg) + ";").append("\n");
            bgStr.append("}").append("\n");
        } else {
            bgStr.append(selector).append("\n");
            bgStr.append("{").append("\n");
            bgStr.append("-fx-background: " + GetRgbString(bg) + ";").append("\n");
            bgStr.append("-fx-base: " + GetRgbString(fg) + ";").append("\n");
            bgStr.append("}").append("\n");
        }
        return bgStr.toString();
    }

    private String InsertBg(String selector, Color bg, boolean bgColor) {
        StringBuilder bgStr = new StringBuilder();
        if (bgColor) {
            bgStr.append(selector).append("\n");
            bgStr.append("{").append("\n");
            bgStr.append("-fx-background-color: " + GetRgbString(bg)  +  ";").append("\n");
            bgStr.append("}").append("\n");
        } else {
            bgStr.append(selector).append("\n");
            bgStr.append("{").append("\n");
            bgStr.append("-fx-background: " + GetRgbString(bg)  +  ";").append("\n");
            bgStr.append("}").append("\n");
        }
        return bgStr.toString();
    }
    private String InsertTxtFill(String selector, Color fg) {
        StringBuilder fgStr = new StringBuilder();
        fgStr.append(selector).append("\n");
        fgStr.append("{").append("\n");
        fgStr.append("-fx-text-fill: " + GetRgbString(fg)  +  ";").append("\n");
        fgStr.append("}").append("\n");
        return fgStr.toString();
    }

    public void StyleFactory() throws FileNotFoundException {
        StringBuilder styleFct = new StringBuilder();
        styleFct.append(InsertTxtFill(".tab .tab-label", this.schemeFg));
        styleFct.append(InsertBg(".tab:top", this.schemeBg, true));
        styleFct.append(InsertBg(".control-buttons-tab", this.schemeBg, true));
        styleFct.append(InsertBg(".tab-content-area", this.schemeBg, true));
        styleFct.append(InsertBgFg(".root", this.schemeBg, this.schemeFg, true));
        styleFct.append(InsertBgFg(".list-view .list-cell", this.schemeBg, this.schemeFg, false));
        styleFct.append(InsertBgFg(".list-view .list-cell:selected", this.schemeFg, this.schemeBg, false));

        PrintStream printStr = new PrintStream(new File("/tmp/fdpanel_style.css"));
        Runtime.getRuntime().addShutdownHook(new Thread(printStr::close));
        printStr.print(styleFct);
        printStr.flush();
    }
}