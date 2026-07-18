package com.smartclinic.desktop.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClinicDesktopApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                ClinicDesktopApplication.class.getResource("/fxml/hello-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Smart Clinic Operations System");
        stage.setScene(scene);
        stage.show();
    }
}
