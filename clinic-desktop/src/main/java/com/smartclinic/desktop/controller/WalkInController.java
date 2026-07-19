package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WalkInController implements NavigationAware {

    @FXML
    private Label statusLabel;

    @Override
    public void onNavigate() {
        statusLabel.setText("Walk-in queue item will be created via POST /api/v1/queue-items.");
    }
}
