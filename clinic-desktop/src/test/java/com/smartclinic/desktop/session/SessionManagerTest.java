package com.smartclinic.desktop.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartclinic.desktop.dto.LoginResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void startSessionShouldStoreAuthorizationHeaderAndUserInfo() {
        SessionManager sessionManager = new SessionManager();
        LoginResponse response = new LoginResponse();
        response.setAccessToken("token-123");
        response.setTokenType("Bearer");
        response.setUserName("admin");
        response.setFullName("System Admin");
        response.setRoles(List.of("ROLE_ADMIN"));

        sessionManager.startSession(response);

        assertTrue(sessionManager.isAuthenticated());
        assertEquals("Bearer token-123", sessionManager.authorizationHeader().orElseThrow());
        assertEquals("admin", sessionManager.getUserName());
        assertEquals("System Admin", sessionManager.getFullName());
        assertEquals(List.of("ROLE_ADMIN"), sessionManager.getRoles());
    }

    @Test
    void clearShouldRemoveSession() {
        SessionManager sessionManager = new SessionManager();
        LoginResponse response = new LoginResponse();
        response.setAccessToken("token-123");
        response.setTokenType("Bearer");
        sessionManager.startSession(response);

        sessionManager.clear();

        assertFalse(sessionManager.isAuthenticated());
        assertTrue(sessionManager.authorizationHeader().isEmpty());
    }
}