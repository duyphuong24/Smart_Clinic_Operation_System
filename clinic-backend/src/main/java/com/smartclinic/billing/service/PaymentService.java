package com.smartclinic.billing.service;

import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.dto.PaymentResponse;
import com.smartclinic.billing.entity.PaymentStatus;
import java.util.List;

public interface PaymentService {

    PaymentResponse record(Long invoiceId, PaymentRequest request);

    List<PaymentResponse> findAll(Long invoiceId, PaymentStatus status);
}
