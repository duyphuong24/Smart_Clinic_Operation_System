package com.smartclinic.billing.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartclinic.billing.dto.InvoiceCancelRequest;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.dto.PayOSPaymentResponse;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.service.InvoiceService;
import com.smartclinic.billing.service.PayOSService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvoiceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private PayOSService payOSService;

    @Test
    @WithMockUser(roles = "CASHIER")
    void findAllInvoices_Success() throws Exception {
        InvoiceResponse response = InvoiceResponse.builder()
                .id(100L)
                .invoiceNumber("INV-20260723-000001")
                .status(InvoiceStatus.ISSUED)
                .totalAmount(new BigDecimal("150000"))
                .build();

        when(invoiceService.findAll(null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].invoiceNumber").value("INV-20260723-000001"));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void getById_Success() throws Exception {
        InvoiceResponse response = InvoiceResponse.builder()
                .id(100L)
                .invoiceNumber("INV-20260723-000001")
                .status(InvoiceStatus.ISSUED)
                .totalAmount(new BigDecimal("150000"))
                .build();

        when(invoiceService.getById(100L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/invoices/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void generateInvoice_Success() throws Exception {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setEncounterId(1L);

        InvoiceResponse response = InvoiceResponse.builder()
                .id(100L)
                .invoiceNumber("INV-20260723-000001")
                .status(InvoiceStatus.ISSUED)
                .totalAmount(new BigDecimal("150000"))
                .build();

        when(invoiceService.generate(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.invoiceNumber").value("INV-20260723-000001"));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void cancelInvoice_Success() throws Exception {
        InvoiceCancelRequest request = new InvoiceCancelRequest();
        request.setReason("Patient cancelled treatment");

        InvoiceResponse response = InvoiceResponse.builder()
                .id(100L)
                .invoiceNumber("INV-20260723-000001")
                .status(InvoiceStatus.CANCELLED)
                .build();

        when(invoiceService.cancel(eq(100L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/invoices/100/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void createPayOSLink_Success() throws Exception {
        InvoiceResponse invoice = InvoiceResponse.builder()
                .id(100L)
                .invoiceNumber("INV-20260723-000001")
                .totalAmount(new BigDecimal("150000"))
                .build();

        PayOSPaymentResponse payosResponse = PayOSPaymentResponse.builder()
                .qrCode("https://img.vietqr.io/image/970422-123456789-compact2.png")
                .orderCode("100")
                .amount(new BigDecimal("150000"))
                .build();

        when(invoiceService.getById(100L)).thenReturn(invoice);
        when(payOSService.createPaymentLink(any())).thenReturn(payosResponse);

        mockMvc.perform(post("/api/v1/invoices/100/payos-link"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderCode").value("100"));
    }
}
