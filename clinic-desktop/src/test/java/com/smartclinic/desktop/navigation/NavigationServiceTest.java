package com.smartclinic.desktop.navigation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartclinic.desktop.config.AppContext;
import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.session.SessionManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NavigationServiceTest {

    private SessionManager sessionManager;
    private NavigationService navigationService;

    @BeforeEach
    void setUp() {
        AppContext appContext = new AppContext();
        sessionManager = appContext.getSessionManager();
        navigationService = appContext.getNavigationService();
    }

    @Test
    void receptionistShouldSeeReceptionRoutesOnly() {
        startSession(List.of("ROLE_RECEPTIONIST"));

        List<DesktopRoute> routes = navigationService.visibleMenuRoutes();

        assertTrue(routes.contains(DesktopRoute.HOME));
        assertTrue(routes.contains(DesktopRoute.TODAY_APPOINTMENTS));
        assertTrue(routes.contains(DesktopRoute.PATIENT_SEARCH));
        assertTrue(routes.contains(DesktopRoute.WALK_IN));
        assertTrue(routes.contains(DesktopRoute.QUEUE_BOARD));
        assertFalse(routes.contains(DesktopRoute.PENDING_INVOICES));
    }

    @Test
    void cashierShouldSeeBillingRoutesOnly() {
        startSession(List.of("ROLE_CASHIER"));

        List<DesktopRoute> routes = navigationService.visibleMenuRoutes();

        assertTrue(routes.contains(DesktopRoute.HOME));
        assertTrue(routes.contains(DesktopRoute.PENDING_INVOICES));
        assertFalse(routes.contains(DesktopRoute.TODAY_APPOINTMENTS));
        assertFalse(routes.contains(DesktopRoute.QUEUE_BOARD));
    }

    @Test
    void navigateShouldDenyUnauthorizedRoute() {
        startSession(List.of("ROLE_CASHIER"));

        NavigationService.NavigationResult result = navigationService.navigate(DesktopRoute.QUEUE_BOARD);

        assertFalse(result.allowed());
        assertEquals(DesktopRoute.QUEUE_BOARD, result.route());
    }

    private void startSession(List<String> roles) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken("token");
        response.setTokenType("Bearer");
        response.setUserName("demo");
        response.setFullName("Demo User");
        response.setRoles(roles);
        sessionManager.startSession(response);
    }
}
