package com.smartclinic.desktop.app;

import com.smartclinic.desktop.config.AppContext;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClinicDesktopApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AppContext appContext = new AppContext();
        FXMLLoader fxmlLoader = new FXMLLoader(
                ClinicDesktopApplication.class.getResource("/fxml/login-view.fxml")
        );
        fxmlLoader.setControllerFactory(appContext::createController);

        Scene scene = new Scene(fxmlLoader.load(), 420, 320);
        stage.setTitle("Smart Clinic Operations System");
        stage.setScene(scene);
        stage.show();
    }
}