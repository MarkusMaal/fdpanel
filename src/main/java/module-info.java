module ee.mas.fdpanel {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.datatransfer;
    requires java.desktop;

    opens ee.mas.fdpanel to javafx.fxml;
    exports ee.mas.fdpanel;
}