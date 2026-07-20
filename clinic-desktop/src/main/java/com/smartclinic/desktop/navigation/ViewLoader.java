package com.smartclinic.desktop.navigation;

import com.smartclinic.desktop.config.AppContext;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewLoader {

    private final AppContext appContext;

    public ViewLoader(AppContext appContext) {
        this.appContext = appContext;
    }

    public LoadedView load(DesktopRoute route) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route.getFxmlPath()),
                "Missing FXML resource: " + route.getFxmlPath()
        );
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(appContext::createController);
        try {
            Parent root = loader.load();
            Object controller = loader.getController();
            return new LoadedView(root, controller);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load view: " + route.getFxmlPath(), ex);
        }
    }

    public record LoadedView(Parent root, Object controller) {
    }
}
