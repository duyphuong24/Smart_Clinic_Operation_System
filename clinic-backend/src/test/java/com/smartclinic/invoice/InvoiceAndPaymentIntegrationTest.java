package com.smartclinic.invoice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.invoice.dto.InvoiceCreateRequest;
import com.smartclinic.payment.dto.PaymentRequest;
import com.smartclinic.payment.entity.PaymentMethod;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoiceAndPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void testGetInvoicesListAsCashierSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void testGetPaymentsListAsCashierSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void testGenerateInvoiceForNonExistentEncounterReturns404() throws Exception {
        InvoiceCreateRequest createRequest = new InvoiceCreateRequest();
        createRequest.setEncounterId(999999L);

        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "cashier", roles = {"CASHIER"})
    void testRecordPaymentForNonExistentInvoiceReturns404() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(new BigDecimal("150000.00"));
        paymentRequest.setMethod(PaymentMethod.CASH);

        mockMvc.perform(post("/api/v1/invoices/999999/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
