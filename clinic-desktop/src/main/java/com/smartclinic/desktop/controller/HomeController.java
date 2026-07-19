package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.RoleUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    private final SessionManager sessionManager;

    @FXML
    private Label welcomeTitle;

    @FXML
    private Label welcomeMessage;

    @FXML
    private Label roleBadge;

    public HomeController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    private void initialize() {
        String fullName = sessionManager.getFullName();
        welcomeTitle.setText("Welcome, " + (fullName == null || fullName.isBlank() ? "User" : fullName));
        welcomeMessage.setText(
                "Use the sidebar to open reception or cashier workflows. "
                        + "Appointment, queue, and billing screens will be connected to the REST API next."
        );
        roleBadge.setText(RoleUtil.primaryRoleLabel(sessionManager.getRoles()));
    }
}
