package com.smartclinic.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.billing.config.PayOSConfig;
import com.smartclinic.billing.dto.PayOSPaymentRequest;
import com.smartclinic.billing.dto.PayOSWebhookData;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private Invoice sampleInvoice;

    @BeforeEach
    void setUp() {
        sampleInvoice = new Invoice();
        sampleInvoice.setInvoiceNumber("INV-001001");
        sampleInvoice.setTotalAmount(BigDecimal.valueOf(250000));
        sampleInvoice.setStatus(InvoiceStatus.ISSUED);
    }

    @Test
    void createPaymentLink_ShouldReturnVietQRData() {
        PayOSPaymentRequest request = PayOSPaymentRequest.builder()
                .invoiceId(1001L)
                .invoiceNumber("INV-001001")
                .amount(BigDecimal.valueOf(250000))
                .description("INV-001001")
                .build();

        var response = payOSService.createPaymentLink(request);

        assertThat(response).isNotNull();
        assertThat(response.getPaymentLinkId()).isEqualTo("PAYOS-1001");
        assertThat(response.getQrCode()).contains("INV-001001");
    }

    @Test
    void verifyWebhookSignature_ShouldReturnTrue_WhenDataIsValid() {
        PayOSWebhookData webhookData = new PayOSWebhookData();
        webhookData.setData(new PayOSWebhookData.Data());

        boolean result = payOSService.verifyWebhookSignature(webhookData);

        assertThat(result).isTrue();
    }

    @Test
    void verifyWebhookSignature_ShouldReturnFalse_WhenDataIsNull() {
        boolean result = payOSService.verifyWebhookSignature(null);

        assertThat(result).isFalse();
    }

    @Test
    void processWebhook_ShouldReconcileInvoiceToPaid() {
        when(invoiceRepository.findById(1001L)).thenReturn(Optional.of(sampleInvoice));

        PayOSWebhookData webhookData = new PayOSWebhookData();
        webhookData.setCode("00");
        webhookData.setDesc("Success");

        PayOSWebhookData.Data innerData = new PayOSWebhookData.Data();
        innerData.setOrderCode(1001L);
        innerData.setCode("00");
        innerData.setAmount(BigDecimal.valueOf(250000));
        innerData.setReference("FT2607229988");
        webhookData.setData(innerData);

        payOSService.processWebhook(webhookData);

        assertThat(sampleInvoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        verify(paymentRepository).save(any());
        verify(invoiceRepository).save(sampleInvoice);
    }
}
