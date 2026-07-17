package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.util.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void initialize() {
        userNameField.setText("admin");
        passwordField.setText("admin123");
    }

    @FXML
    private void onLogin() {
        String userName = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (userName.isBlank() || password.isBlank()) {
            statusLabel.setText("Enter user name and password.");
            return;
        }

        setLoading(true);
        authService.login(userName, password)
                .whenComplete((response, throwable) -> Platform.runLater(() -> handleLoginResult(response, throwable)));
    }

    private void handleLoginResult(LoginResponse response, Throwable throwable) {
        setLoading(false);
        if (throwable != null) {
            statusLabel.setText(AlertUtil.userMessage(throwable));
            return;
        }

        statusLabel.setText("Logged in as " + response.getFullName() + " (" + String.join(", ", response.getRoles()) + ")");
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        userNameField.setDisable(loading);
        passwordField.setDisable(loading);
        statusLabel.setText(loading ? "Signing in..." : "");
    }
}