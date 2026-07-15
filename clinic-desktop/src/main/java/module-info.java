module com.smartclinic.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens com.smartclinic.desktop.app to javafx.fxml;
    opens com.smartclinic.desktop.controller to javafx.fxml;

    exports com.smartclinic.desktop.app;
}
