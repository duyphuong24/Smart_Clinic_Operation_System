package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PaymentController implements NavigationAware {

    @FXML
    private Label statusLabel;

    @Override
    public void onNavigate() {
        statusLabel.setText("Payment recording will call POST /api/v1/invoices/{id}/payments.");
    }
}
