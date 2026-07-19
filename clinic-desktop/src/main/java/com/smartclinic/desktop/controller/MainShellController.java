package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.SceneNavigator;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.RoleUtil;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainShellController {

    private final SessionManager sessionManager;
    private final AuthService authService;
    private final SceneNavigator sceneNavigator;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox sidebarMenu;

    @FXML
    private Button homeMenuButton;

    @FXML
    private Button appointmentsMenuButton;

    @FXML
    private Button patientsMenuButton;

    @FXML
    private Button queueMenuButton;

    @FXML
    private Button invoicesMenuButton;

    public MainShellController(
            SessionManager sessionManager,
            AuthService authService,
            SceneNavigator sceneNavigator
    ) {
        this.sessionManager = sessionManager;
        this.authService = authService;
        this.sceneNavigator = sceneNavigator;
    }

    @FXML
    private void initialize() {
        userNameLabel.setText(sessionManager.getFullName());
        userRoleLabel.setText(RoleUtil.primaryRoleLabel(sessionManager.getRoles()));
        configureRoleBasedMenu();
        showHome();
    }

    @FXML
    private void onHome() {
        showHome();
    }

    @FXML
    private void onAppointments() {
        setActiveMenu(appointmentsMenuButton);
        pageTitleLabel.setText("Today Appointments");
        showPlaceholder("Today Appointments screen will load appointment data from /api/v1/appointments/today.");
    }

    @FXML
    private void onPatients() {
        setActiveMenu(patientsMenuButton);
        pageTitleLabel.setText("Patient Search");
        showPlaceholder("Patient search will call /api/v1/patients/search.");
    }

    @FXML
    private void onQueue() {
        setActiveMenu(queueMenuButton);
        pageTitleLabel.setText("Queue Board");
        showPlaceholder("Queue board will call /api/v1/queue-items/today.");
    }

    @FXML
    private void onInvoices() {
        setActiveMenu(invoicesMenuButton);
        pageTitleLabel.setText("Pending Invoices");
        showPlaceholder("Pending invoices will call /api/v1/invoices/pending.");
    }

    @FXML
    private void onLogout() {
        authService.logout();
        sceneNavigator.showLogin();
    }

    private void showHome() {
        setActiveMenu(homeMenuButton);
        pageTitleLabel.setText("Home");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            loader.setController(new HomeController(sessionManager));
            Parent homeView = loader.load();
            contentPane.getChildren().setAll(homeView);
        } catch (IOException ex) {
            showPlaceholder("Unable to load home view.");
        }
    }

    private void showPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("home-subtitle");
        placeholder.setWrapText(true);
        contentPane.getChildren().setAll(placeholder);
    }

    private void configureRoleBasedMenu() {
        var roles = sessionManager.getRoles();
        appointmentsMenuButton.setVisible(RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_RECEPTIONIST"));
        appointmentsMenuButton.setManaged(appointmentsMenuButton.isVisible());
        patientsMenuButton.setVisible(RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_RECEPTIONIST"));
        patientsMenuButton.setManaged(patientsMenuButton.isVisible());
        queueMenuButton.setVisible(RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_DOCTOR"));
        queueMenuButton.setManaged(queueMenuButton.isVisible());
        invoicesMenuButton.setVisible(RoleUtil.hasAnyRole(roles, "ROLE_ADMIN", "ROLE_CASHIER"));
        invoicesMenuButton.setManaged(invoicesMenuButton.isVisible());
    }

    private void setActiveMenu(Button activeButton) {
        for (var node : sidebarMenu.getChildren()) {
            if (node instanceof Button button) {
                button.getStyleClass().remove("menu-button-active");
                if (!button.getStyleClass().contains("menu-button")) {
                    button.getStyleClass().add("menu-button");
                }
            }
        }
        activeButton.getStyleClass().remove("menu-button");
        if (!activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }
    }
}
