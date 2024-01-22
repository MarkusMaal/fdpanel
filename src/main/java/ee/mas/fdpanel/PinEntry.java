package ee.mas.fdpanel;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PinEntry {

    @FXML
    private TextField pDsp;
    private MainApp mainApp;
    private Stage dialogStage;

    private String pin = "";


    public void setMainApp(MainApp m) {this.mainApp = m;}
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void updatePinDisplay() {
        pDsp.setText("*".repeat(pin.length()));
    }


    @FXML
    private void initialize() {

    }
    @FXML
    private void endClicked() {
        dialogStage.close();
    }



    @FXML
    private void oneClicked() {  pin = pin + "1"; updatePinDisplay(); }
    @FXML
    private void twoClicked() {  pin = pin + "2"; updatePinDisplay(); }
    @FXML
    private void threeClicked() {  pin = pin + "3"; updatePinDisplay(); }
    @FXML
    private void fourClicked() {  pin = pin + "4"; updatePinDisplay(); }
    @FXML
    private void fiveClicked() {  pin = pin + "5"; updatePinDisplay(); }
    @FXML
    private void sixClicked() {  pin = pin + "6"; updatePinDisplay(); }
    @FXML
    private void sevenClicked() {  pin = pin + "7"; updatePinDisplay(); }
    @FXML
    private void eightClicked() {  pin = pin + "8"; updatePinDisplay(); }
    @FXML
    private void nineClicked() {  pin = pin + "9"; updatePinDisplay(); }
    @FXML
    private void noughtClicked() {  pin = pin + "0"; updatePinDisplay(); }
    @FXML
    private void bspClicked() {  pin = pin.substring(0, pin.length() - 1); updatePinDisplay(); }

    public String getPin() { if (mainApp.platform.isEmpty()) { return ""; }
        return this.pin;
    }

}
