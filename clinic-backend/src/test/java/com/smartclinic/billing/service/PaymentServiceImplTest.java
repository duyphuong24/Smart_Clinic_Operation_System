package com.smartclinic.billing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.dto.PaymentResponse;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.entity.Payment;
import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.common.exception.BadRequestException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl service;

    @Test
    void recordShouldRejectNonIssuedInvoice() {
        Invoice invoice = invoice(InvoiceStatus.PAID, BigDecimal.valueOf(150000));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> service.record(1L, request(BigDecimal.valueOf(150000), PaymentStatus.SUCCESS)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only ISSUED invoice can be paid");
    }

    @Test
    void recordShouldRejectAmountDifferentFromInvoiceTotal() {
        Invoice invoice = invoice(InvoiceStatus.ISSUED, BigDecimal.valueOf(150000));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> service.record(1L, request(BigDecimal.valueOf(100000), PaymentStatus.SUCCESS)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Payment amount must equal invoice total");
    }

    @Test
    void recordSuccessShouldMarkInvoicePaid() {
        Invoice invoice = invoice(InvoiceStatus.ISSUED, BigDecimal.valueOf(150000));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(9L);
            return payment;
        });

        PaymentResponse response = service.record(1L, request(BigDecimal.valueOf(150000), PaymentStatus.SUCCESS));

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void recordFailedShouldNotMarkInvoicePaid() {
        Invoice invoice = invoice(InvoiceStatus.ISSUED, BigDecimal.valueOf(150000));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(9L);
            return payment;
        });

        PaymentResponse response = service.record(1L, request(BigDecimal.valueOf(150000), PaymentStatus.FAILED));

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.ISSUED);
        verify(invoiceRepository, never()).save(invoice);
    }

    private PaymentRequest request(BigDecimal amount, PaymentStatus status) {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);
        request.setMethod(PaymentMethod.BANK_TRANSFER);
        request.setStatus(status);
        request.setTransactionRef(" TXN-001 ");
        request.setNote(" Paid at counter ");
        return request;
    }

    private Invoice invoice(InvoiceStatus status, BigDecimal totalAmount) {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV-20260718-000001");
        invoice.setStatus(status);
        invoice.setTotalAmount(totalAmount);
        return invoice;
    }
}
