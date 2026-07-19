package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.NavigationAware;
import com.smartclinic.desktop.navigation.NavigationService;
import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.RoleUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController implements NavigationAware {

    private final SessionManager sessionManager;
    private final NavigationService navigationService;

    @FXML
    private Label welcomeTitle;

    @FXML
    private Label welcomeMessage;

    @FXML
    private Label roleBadge;

    public HomeController(SessionManager sessionManager, NavigationService navigationService) {
        this.sessionManager = sessionManager;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        refreshContent();
    }

    @Override
    public void onNavigate() {
        refreshContent();
    }

    private void refreshContent() {
        String fullName = sessionManager.getFullName();
        welcomeTitle.setText("Welcome, " + (fullName == null || fullName.isBlank() ? "User" : fullName));
        welcomeMessage.setText(buildWelcomeMessage());
        roleBadge.setText(RoleUtil.primaryRoleLabel(sessionManager.getRoles()));
    }

    private String buildWelcomeMessage() {
        var routes = navigationService.visibleMenuRoutes().stream()
                .filter(route -> route != com.smartclinic.desktop.navigation.DesktopRoute.HOME)
                .map(com.smartclinic.desktop.navigation.DesktopRoute::getPageTitle)
                .toList();
        if (routes.isEmpty()) {
            return "Your account does not have desktop menu permissions.";
        }
        return "Available workflows: " + String.join(", ", routes) + ".";
    }
}
