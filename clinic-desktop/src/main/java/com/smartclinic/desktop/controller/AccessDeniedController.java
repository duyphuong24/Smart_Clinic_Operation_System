package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.DesktopRoute;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AccessDeniedController {

    private DesktopRoute deniedRoute;

    @FXML
    private Label messageLabel;

    public void setDeniedRoute(DesktopRoute deniedRoute) {
        this.deniedRoute = deniedRoute;
        if (messageLabel != null) {
            updateMessage();
        }
    }

    @FXML
    private void initialize() {
        updateMessage();
    }

    private void updateMessage() {
        if (deniedRoute == null) {
            messageLabel.setText("You do not have permission to open this screen.");
            return;
        }
        messageLabel.setText("You do not have permission to open \"" + deniedRoute.getPageTitle() + "\".");
    }
}
