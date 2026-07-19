package com.smartclinic.desktop.app;

import com.smartclinic.desktop.config.AppContext;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClinicDesktopApplication extends Application {

    @Override
    public void start(Stage stage) {
        AppContext appContext = new AppContext();
        appContext.bindStage(stage);
        appContext.getSceneNavigator().showLogin();
    }
}
