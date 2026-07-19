package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PendingInvoicesController implements NavigationAware {

    @FXML
    private Label statusLabel;

    @Override
    public void onNavigate() {
        statusLabel.setText("Pending invoices will load from GET /api/v1/invoices/pending.");
    }
}
