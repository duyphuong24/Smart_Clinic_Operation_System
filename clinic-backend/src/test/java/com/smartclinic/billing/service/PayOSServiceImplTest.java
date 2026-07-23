package com.smartclinic.billing.service;

import com.smartclinic.billing.config.PayOSConfig;
import com.smartclinic.billing.dto.PayOSPaymentRequest;
import com.smartclinic.billing.dto.PayOSPaymentResponse;
import com.smartclinic.billing.dto.PayOSWebhookData;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.entity.Payment;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayOSServiceImplTest {

    @Mock
    private PayOSConfig payOSConfig;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PayOSServiceImpl payOSService;

    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setId(100L);
        testInvoice.setInvoiceNumber("INV-2026-0001");
        testInvoice.setTotalAmount(new BigDecimal("250000.00"));
        testInvoice.setStatus(InvoiceStatus.ISSUED);
    }

    @Test
    @DisplayName("createPaymentLink - should return valid PayOS dynamic VietQR response")
    void createPaymentLink_Success() {
        PayOSPaymentRequest request = PayOSPaymentRequest.builder()
                .invoiceId(100L)
                .invoiceNumber("INV-2026-0001")
                .amount(new BigDecimal("250000.00"))
                .description("Thanh toan hoa don INV-2026-0001")
                .build();

        PayOSPaymentResponse response = payOSService.createPaymentLink(request);

        assertNotNull(response);
        assertEquals("100", response.getOrderCode());
        assertNotNull(response.getQrCode());
        assertTrue(response.getQrCode().contains("250000.00"));
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    @DisplayName("processWebhook - should process valid webhook and update invoice to PAID")
    void processWebhook_Success() {
        PayOSWebhookData.Data webhookInnerData = new PayOSWebhookData.Data();
        webhookInnerData.setOrderCode(100L);
        webhookInnerData.setAmount(new BigDecimal("250000.00"));
        webhookInnerData.setCode("00");
        webhookInnerData.setReference("PAYOS-REF-999");
        webhookInnerData.setDescription("INV-2026-0001");

        PayOSWebhookData webhookData = new PayOSWebhookData();
        webhookData.setCode("00");
        webhookData.setDesc("Success");
        webhookData.setData(webhookInnerData);

        when(invoiceRepository.findById(100L)).thenReturn(Optional.of(testInvoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        payOSService.processWebhook(webhookData);

        assertEquals(InvoiceStatus.PAID, testInvoice.getStatus());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertNotNull(savedPayment);
        assertEquals(new BigDecimal("250000.00"), savedPayment.getAmount());
        assertEquals("PAYOS-REF-999", savedPayment.getTransactionRef());
    }
}
