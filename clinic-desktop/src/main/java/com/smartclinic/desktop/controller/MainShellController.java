package com.smartclinic.desktop.controller;

import com.smartclinic.desktop.navigation.DesktopRoute;
import com.smartclinic.desktop.navigation.NavigationService;
import com.smartclinic.desktop.navigation.SceneNavigator;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.session.SessionManager;
import com.smartclinic.desktop.util.AlertUtil;
import com.smartclinic.desktop.util.RoleUtil;
import java.util.EnumMap;
import java.util.Map;
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
    private final NavigationService navigationService;

    private final Map<DesktopRoute, Button> menuButtons = new EnumMap<>(DesktopRoute.class);

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Label breadcrumbLabel;

    @FXML
    private Label navbarUserLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox sidebarMenu;

    public MainShellController(
            SessionManager sessionManager,
            AuthService authService,
            SceneNavigator sceneNavigator,
            NavigationService navigationService
    ) {
        this.sessionManager = sessionManager;
        this.authService = authService;
        this.sceneNavigator = sceneNavigator;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        userNameLabel.setText(sessionManager.getFullName());
        userRoleLabel.setText(RoleUtil.primaryRoleLabel(sessionManager.getRoles()));
        navbarUserLabel.setText(sessionManager.getUserName());
        navigationService.setRouteChangeListener(this::navigateTo);
        buildSidebarMenu();
        navigateTo(DesktopRoute.HOME);
    }

    @FXML
    private void onLogout() {
        authService.logout();
        sceneNavigator.showLogin();
    }

    public void navigateTo(DesktopRoute route) {
        NavigationService.NavigationResult result = navigationService.navigate(route);
        if (!result.allowed()) {
            showAccessDenied(route);
            updateHeader(route);
            setActiveMenu(null);
            return;
        }

        contentPane.getChildren().setAll(result.content());
        updateHeader(route);
        setActiveMenu(route);
    }

    private void buildSidebarMenu() {
        sidebarMenu.getChildren().clear();
        menuButtons.clear();

        for (DesktopRoute route : navigationService.visibleMenuRoutes()) {
            Button menuButton = new Button(route.getPageTitle());
            menuButton.setMaxWidth(Double.MAX_VALUE);
            menuButton.getStyleClass().add("menu-button");
            menuButton.setOnAction(event -> navigateTo(route));
            sidebarMenu.getChildren().add(menuButton);
            menuButtons.put(route, menuButton);
        }
    }

    private void updateHeader(DesktopRoute route) {
        pageTitleLabel.setText(route.getPageTitle());
        breadcrumbLabel.setText(route.getBreadcrumb());
    }

    private void setActiveMenu(DesktopRoute active) {
        menuButtons.forEach((route, button) -> {
            button.getStyleClass().remove("menu-button-active");
            if (!button.getStyleClass().contains("menu-button")) {
                button.getStyleClass().add("menu-button");
            }
        });

        if (active == null) {
            return;
        }

        Button activeButton = menuButtons.get(active);
        if (activeButton == null) {
            return;
        }
        activeButton.getStyleClass().remove("menu-button");
        if (!activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }
    }

    private void showAccessDenied(DesktopRoute route) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/access-denied-view.fxml"));
            AccessDeniedController controller = new AccessDeniedController();
            controller.setDeniedRoute(route);
            loader.setController(controller);
            Parent accessDeniedView = loader.load();
            contentPane.getChildren().setAll(accessDeniedView);
            AlertUtil.showError("Access Denied", new IllegalStateException(
                    "You do not have permission to open \"" + route.getPageTitle() + "\"."
            ));
        } catch (Exception ex) {
            Label fallback = new Label("You do not have permission to open this screen.");
            fallback.getStyleClass().add("error-label");
            fallback.setWrapText(true);
            contentPane.getChildren().setAll(fallback);
        }
    }
}
