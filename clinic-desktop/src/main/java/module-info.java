module com.smartclinic.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.smartclinic.desktop.app to javafx.fxml;
    opens com.smartclinic.desktop.controller to javafx.fxml;
    opens com.smartclinic.desktop.dto to com.fasterxml.jackson.databind;

    exports com.smartclinic.desktop.app;
    exports com.smartclinic.desktop.config;
    exports com.smartclinic.desktop.navigation;
    exports com.smartclinic.desktop.service;
    exports com.smartclinic.desktop.session;
}