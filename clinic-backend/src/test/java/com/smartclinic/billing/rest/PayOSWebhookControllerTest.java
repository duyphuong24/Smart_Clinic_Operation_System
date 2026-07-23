package com.smartclinic.billing.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.billing.dto.PayOSWebhookData;
import com.smartclinic.billing.service.PayOSService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PayOSWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PayOSService payOSService;

    @Test
    void handlePayOSWebhook_ShouldProcessSuccessfully_WhenSignatureIsValid() throws Exception {
        when(payOSService.verifyWebhookSignature(any())).thenReturn(true);

        PayOSWebhookData webhookData = new PayOSWebhookData();
        webhookData.setCode("00");

        mockMvc.perform(post("/api/v1/billing/payos-webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webhookData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void handlePayOSWebhook_ShouldReturnError_WhenSignatureIsInvalid() throws Exception {
        when(payOSService.verifyWebhookSignature(any())).thenReturn(false);

        PayOSWebhookData webhookData = new PayOSWebhookData();

        mockMvc.perform(post("/api/v1/billing/payos-webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webhookData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
