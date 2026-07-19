package com.smartclinic.desktop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.desktop.api.ApiClient;
import com.smartclinic.desktop.api.AppointmentApiClient;
import com.smartclinic.desktop.api.AuthApiClient;
import com.smartclinic.desktop.api.QueueApiClient;
import com.smartclinic.desktop.controller.HomeController;
import com.smartclinic.desktop.controller.InvoiceDetailController;
import com.smartclinic.desktop.controller.LoginController;
import com.smartclinic.desktop.controller.MainShellController;
import com.smartclinic.desktop.controller.PatientSearchController;
import com.smartclinic.desktop.controller.PaymentController;
import com.smartclinic.desktop.controller.PendingInvoicesController;
import com.smartclinic.desktop.controller.QueueBoardController;
import com.smartclinic.desktop.controller.TodayAppointmentsController;
import com.smartclinic.desktop.controller.WalkInController;
import com.smartclinic.desktop.navigation.NavigationService;
import com.smartclinic.desktop.navigation.SceneNavigator;
import com.smartclinic.desktop.navigation.ViewLoader;
import com.smartclinic.desktop.service.AppointmentDesktopService;
import com.smartclinic.desktop.service.AuthService;
import com.smartclinic.desktop.session.SessionManager;
import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import javafx.stage.Stage;

public class AppContext {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";

    private final SessionManager sessionManager;
    private final AuthService authService;
    private final AppointmentDesktopService appointmentDesktopService;
    private final NavigationService navigationService;
    private SceneNavigator sceneNavigator;

    public AppContext() {
        this.sessionManager = new SessionManager();
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        ApiClient apiClient = new ApiClient(httpClient, objectMapper, sessionManager, resolveBaseUrl());

        AuthApiClient authApiClient = new AuthApiClient(apiClient);
        AppointmentApiClient appointmentApiClient = new AppointmentApiClient(apiClient);
        QueueApiClient queueApiClient = new QueueApiClient(apiClient);

        this.authService = new AuthService(authApiClient, sessionManager);
        this.appointmentDesktopService = new AppointmentDesktopService(appointmentApiClient, queueApiClient);

        ViewLoader viewLoader = new ViewLoader(this);
        this.navigationService = new NavigationService(viewLoader, sessionManager);
    }

    public void bindStage(Stage stage) {
        this.sceneNavigator = new SceneNavigator(stage, this);
    }

    public Object createController(Class<?> controllerClass) {
        if (controllerClass == LoginController.class) {
            return new LoginController(authService, sceneNavigator);
        }
        if (controllerClass == MainShellController.class) {
            return new MainShellController(sessionManager, authService, sceneNavigator, navigationService);
        }
        if (controllerClass == HomeController.class) {
            return new HomeController(sessionManager, navigationService);
        }
        if (controllerClass == TodayAppointmentsController.class) {
            return new TodayAppointmentsController(appointmentDesktopService);
        }
        if (controllerClass == PatientSearchController.class) {
            return new PatientSearchController();
        }
        if (controllerClass == WalkInController.class) {
            return new WalkInController();
        }
        if (controllerClass == QueueBoardController.class) {
            return new QueueBoardController();
        }
        if (controllerClass == PendingInvoicesController.class) {
            return new PendingInvoicesController();
        }
        if (controllerClass == InvoiceDetailController.class) {
            return new InvoiceDetailController();
        }
        if (controllerClass == PaymentController.class) {
            return new PaymentController();
        }

        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new IllegalStateException("Unable to create controller: " + controllerClass.getName(), ex);
        }
    }

    public SceneNavigator getSceneNavigator() {
        return sceneNavigator;
    }

    public NavigationService getNavigationService() {
        return navigationService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthService getAuthService() {
        return authService;
    }

    private String resolveBaseUrl() {
        String propertyValue = System.getProperty("smartclinic.api.base-url");
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv("SMARTCLINIC_API_BASE_URL");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return DEFAULT_BASE_URL;
    }
}
