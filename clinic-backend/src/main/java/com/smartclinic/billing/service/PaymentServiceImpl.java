package com.smartclinic.billing.service;

import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.dto.PaymentResponse;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.billing.entity.Payment;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.mapper.PaymentMapper;
import com.smartclinic.billing.repository.InvoiceRepository;
import com.smartclinic.billing.repository.PaymentRepository;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PaymentServiceImpl implements PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse record(Long invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new BadRequestException("Only ISSUED invoice can be paid");
        }
        if (request.getAmount().compareTo(invoice.getTotalAmount()) != 0) {
            throw new BadRequestException("Payment amount must equal invoice total");
        }

        PaymentStatus status = request.getStatus() == null ? PaymentStatus.SUCCESS : request.getStatus();
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(status);
        payment.setTransactionRef(trimToNull(request.getTransactionRef()));
        payment.setNote(trimToNull(request.getNote()));
        payment.setPaidAt(LocalDateTime.now());

        if (status == PaymentStatus.SUCCESS) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        return PaymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll(Long invoiceId, PaymentStatus status) {
        List<Payment> payments;
        if (invoiceId != null) {
            payments = paymentRepository.findByInvoiceId(invoiceId);
        } else if (status != null) {
            payments = paymentRepository.findByStatus(status);
        } else {
            payments = paymentRepository.findAll();
        }
        return payments.stream().map(PaymentMapper::toResponse).toList();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
