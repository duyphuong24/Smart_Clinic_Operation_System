package com.smartclinic.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.auth.dto.LoginRequest;
import com.smartclinic.auth.dto.TokenRefreshRequest;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSuccessfulLoginAndTokenRetrieval() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("admin");
        loginRequest.setPassword("admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.userName").value("admin"));
    }

    @Test
    void testFailedLoginAttemptsLockAccountAfter5Times() throws Exception {
        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setUserName("admin");
        invalidLogin.setPassword("wrongpassword");

        // Perform 4 failed logins
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidLogin)))
                    .andExpect(status().isUnauthorized());
        }

        // 5th failed attempt triggers account lock
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());

        User adminUser = userRepository.findByUserName("admin").orElseThrow();
        assertEquals(UserStatus.LOCKED, adminUser.getStatus());
        assertNotNull(adminUser.getLockTime());

        // Unlock account for subsequent tests
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setFailedLoginAttempts(0);
        adminUser.setLockTime(null);
        userRepository.save(adminUser);
    }

    @Test
    void testTokenRefreshAndLogoutWorkflow() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("receptionist");
        loginRequest.setPassword("receptionist123");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(jsonResponse).path("data").path("refreshToken").asText();

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);

        // Test Refresh Token
        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn();

        String refreshJsonResponse = refreshResult.getResponse().getContentAsString();
        String newAccessToken = objectMapper.readTree(refreshJsonResponse).path("data").path("accessToken").asText();
        String newRefreshToken = objectMapper.readTree(refreshJsonResponse).path("data").path("refreshToken").asText();

        // Test Logout with new refresh token and Authorization header
        TokenRefreshRequest logoutRequest = new TokenRefreshRequest();
        logoutRequest.setRefreshToken(newRefreshToken);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + newAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}
