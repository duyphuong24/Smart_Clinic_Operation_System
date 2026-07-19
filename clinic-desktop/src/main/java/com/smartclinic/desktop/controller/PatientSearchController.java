package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PatientSearchController implements NavigationAware {

    @FXML
    private Label statusLabel;

    @Override
    public void onNavigate() {
        statusLabel.setText("Ready to search patients via GET /api/v1/patients/search?keyword=.");
    }
}
