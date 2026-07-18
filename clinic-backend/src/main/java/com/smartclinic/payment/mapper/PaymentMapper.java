package com.smartclinic.payment.mapper;

import com.smartclinic.payment.dto.PaymentResponse;
import com.smartclinic.payment.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNumber(payment.getInvoice().getInvoiceNumber())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .note(payment.getNote())
                .paidAt(payment.getPaidAt())
                .build();
    }
}