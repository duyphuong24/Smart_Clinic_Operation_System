package com.smartclinic.payment.service;

import com.smartclinic.payment.dto.PaymentRequest;
import com.smartclinic.payment.dto.PaymentResponse;
import com.smartclinic.payment.entity.PaymentStatus;
import java.util.List;

public interface PaymentService {

    PaymentResponse record(Long invoiceId, PaymentRequest request);

    List<PaymentResponse> findAll(Long invoiceId, PaymentStatus status);
}