package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.navigation.SceneNavigator;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.util.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService;
    private final SceneNavigator sceneNavigator;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    public LoginController(AuthService authService, SceneNavigator sceneNavigator) {
        this.authService = authService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    private void initialize() {
        hideError();
        userNameField.requestFocus();
    }

    @FXML
    private void onLogin() {
        String userName = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (userName.isBlank() || password.isBlank()) {
            showError("Enter user name and password.");
            return;
        }

        setLoading(true);
        authService.login(userName, password)
                .whenComplete((response, throwable) -> Platform.runLater(() -> handleLoginResult(response, throwable)));
    }

    private void handleLoginResult(LoginResponse response, Throwable throwable) {
        setLoading(false);
        if (throwable != null) {
            showError(AlertUtil.userMessage(throwable));
            passwordField.clear();
            passwordField.requestFocus();
            return;
        }

        hideError();
        sceneNavigator.showMainShell();
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        userNameField.setDisable(loading);
        passwordField.setDisable(loading);
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
