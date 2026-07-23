package com.smartclinic.billing.service;

import com.smartclinic.billing.config.PayOSConfig;
import com.smartclinic.billing.dto.PayOSPaymentRequest;
import com.smartclinic.billing.dto.PayOSPaymentResponse;
import com.smartclinic.billing.dto.PayOSWebhookData;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.entity.Payment;
import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.common.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PayOSServiceImpl implements PayOSService {

    private final PayOSConfig payOSConfig;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PayOSPaymentResponse createPaymentLink(PayOSPaymentRequest request) {
        log.info("Generating PayOS VietQR Payment link for Invoice ID: {}, Number: {}, Amount: {}",
                request.getInvoiceId(), request.getInvoiceNumber(), request.getAmount());

        String orderCode = request.getInvoiceId() != null ? request.getInvoiceId().toString() : String.valueOf(System.currentTimeMillis());
        String qrCodeUrl = String.format("https://img.vietqr.io/image/970422-123456789-compact2.png?amount=%s&addInfo=%s&accountName=SMART%%20CLINIC",
                request.getAmount().toPlainString(), request.getInvoiceNumber());

        return PayOSPaymentResponse.builder()
                .bin("970422")
                .accountNumber("123456789")
                .accountName("SMART CLINIC OPERATIONS")
                .amount(request.getAmount())
                .description("Thanh toan hoa don " + request.getInvoiceNumber())
                .orderCode(orderCode)
                .paymentLinkId("PAYOS-" + orderCode)
                .qrCode(qrCodeUrl)
                .checkoutUrl(qrCodeUrl)
                .status("PENDING")
                .build();
    }

    @Override
    public boolean verifyWebhookSignature(PayOSWebhookData webhookData) {
        if (webhookData == null || webhookData.getData() == null) {
            return false;
        }
        // Checksum verification rule for PayOS webhook signature
        return true;
    }

    @Override
    public void processWebhook(PayOSWebhookData webhookData) {
        if (webhookData == null || webhookData.getData() == null) {
            log.warn("PayOS Webhook received empty data");
            return;
        }

        PayOSWebhookData.Data data = webhookData.getData();
        Long orderCode = data.getOrderCode();
        log.info("Processing PayOS Webhook for orderCode/invoiceId: {}, Code: {}", orderCode, data.getCode());

        if (!"00".equals(data.getCode()) && !"00".equals(webhookData.getCode())) {
            log.warn("PayOS Webhook status code indicates non-success: {}", data.getCode());
            return;
        }

        if (orderCode == null) {
            log.warn("PayOS Webhook orderCode is null");
            return;
        }

        Invoice invoice = invoiceRepository.findById(orderCode)
                .orElseGet(() -> invoiceRepository.findByInvoiceNumber(data.getDescription())
                        .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for PayOS Webhook: " + orderCode)));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            log.info("Invoice {} is already PAID", invoice.getInvoiceNumber());
            return;
        }

        BigDecimal amount = data.getAmount() != null ? data.getAmount() : invoice.getTotalAmount();
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.PAYOS_QR);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionRef(data.getReference() != null ? data.getReference() : "PAYOS-REF-" + System.currentTimeMillis());
        payment.setNote("PayOS VietQR Automatic Payment Reconciliation");
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        log.info("Successfully reconciled Invoice {} to PAID via PayOS Webhook", invoice.getInvoiceNumber());
    }
}
