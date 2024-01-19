package ee.mas.fdpanel;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class MainApp extends Application {
    public Stage primaryStage;
    private BorderPane rootLayout;

    public InnerLayout mainWindow;

    public FlashDrive fd;

    public String version = "0.0";

    public List<FlashDrive> drives = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Markuse mälupulk");
        initRootLayout();
        findFlashDrives();
        if (!this.drives.isEmpty()) {
            int idx = 0;
            if (this.drives.size() > 1) {
                idx = showDriveChooserDialog();
            }
            fd = this.drives.get(idx);
        } else {
            System.out.println("No flash drives detected!");
            System.exit(0);
        }
        showFirstForm();
    }

    public void ReloadDevices(boolean chooseDev) throws IOException {
        this.drives.clear();
        int idx = 0;
        findFlashDrives();
        if (chooseDev) {
            idx = showDriveChooserDialog();
        } else {
            for (int i = 0; i < this.drives.size(); i++) {
                if (this.drives.get(i).GetDiskSize().equals(this.fd.GetDiskSize()) && this.drives.get(i).GetDevice().equals(this.fd.GetDevice())) {
                    idx = i;
                }
            }
        }
        if (!this.drives.isEmpty()) {
            fd = this.drives.get(idx);
        } else {
            System.out.println("No flash drives detected!");
            System.exit(0);
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
                FlashDrive test = new FlashDrive("/run/media/markus/" + drv);
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
        launch();
    }

    private Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setFullscreen(Boolean b) {
        this.primaryStage.setFullScreen(b);
    }

    public int showDriveChooserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("DriveChooser.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
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
}