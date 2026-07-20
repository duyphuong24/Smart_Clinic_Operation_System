package com.smartclinic.desktop.navigation;

import com.smartclinic.desktop.config.AppContext;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator {

    private static final int LOGIN_WIDTH = 480;
    private static final int LOGIN_HEIGHT = 640;
    private static final int MAIN_WIDTH = 1280;
    private static final int MAIN_HEIGHT = 800;

    private final Stage stage;
    private final AppContext appContext;

    public SceneNavigator(Stage stage, AppContext appContext) {
        this.stage = stage;
        this.appContext = appContext;
    }

    public void showLogin() {
        Parent root = loadView("/fxml/login-view.fxml");
        applyScene(root, LOGIN_WIDTH, LOGIN_HEIGHT);
        stage.setTitle("Smart Clinic - Sign In");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public void showMainShell() {
        Parent root = loadView("/fxml/main-shell.fxml");
        applyScene(root, MAIN_WIDTH, MAIN_HEIGHT);
        stage.setTitle("Smart Clinic Operations System");
        stage.setResizable(true);
        stage.setMinWidth(1024);
        stage.setMinHeight(680);
        stage.centerOnScreen();
        stage.show();
    }

    private Parent loadView(String resourcePath) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(resourcePath),
                "Missing FXML resource: " + resourcePath
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(appContext::createController);
        try {
            return loader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load view: " + resourcePath, ex);
        }
    }

    private void applyScene(Parent root, double width, double height) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root, width, height);
            URL stylesheet = getClass().getResource("/css/app.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }
            stage.setScene(scene);
            return;
        }

        scene.setRoot(root);
        stage.setWidth(width);
        stage.setHeight(height);
    }
}
