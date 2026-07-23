package com.smartclinic.billing.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayOSPaymentRequest {

    private final Long invoiceId;
    private final String invoiceNumber;
    private final BigDecimal amount;
    private final String description;
    private final String cancelUrl;
    private final String returnUrl;
}
