package com.smartclinic.billing.dto;

import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private final Long id;
    private final Long invoiceId;
    private final String invoiceNumber;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final String transactionRef;
    private final String note;
    private final LocalDateTime paidAt;
}
