package com.smartclinic.billing.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.dto.PaymentResponse;
import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.service.PaymentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("POST /api/v1/invoices/{id}/payments - should record payment successfully")
    void recordPayment_Success() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("150000.00"));
        request.setMethod(PaymentMethod.CASH);
        request.setNote("Cash payment at POS cashier");

        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(10L)
                .invoiceId(100L)
                .amount(new BigDecimal("150000.00"))
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentService.record(eq(100L), any(PaymentRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/invoices/100/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.method").value("CASH"));
    }
}
