package com.smartclinic.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.auth.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicEndpointsShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        LoginRequest invalidLogin = new LoginRequest();
        invalidLogin.setUserName("");
        invalidLogin.setPassword("");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void protectedEndpointsShouldReturn401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void endpointShouldReturn403WhenWrongRole() throws Exception {
        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void endpointShouldBeAllowedWhenCorrectRole() throws Exception {
        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    void auditLogsShouldRejectUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void managerShouldAccessAuditLogsInvoicesAndPayments() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk());
    }
}
