package com.smartclinic.billing.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayOSPaymentResponse {

    private final String bin;
    private final String accountNumber;
    private final String accountName;
    private final BigDecimal amount;
    private final String description;
    private final String orderCode;
    private final String paymentLinkId;
    private final String qrCode;
    private final String checkoutUrl;
    private final String status;
}
