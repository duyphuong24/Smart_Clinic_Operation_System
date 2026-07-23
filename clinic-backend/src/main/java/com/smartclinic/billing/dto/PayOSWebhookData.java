package com.smartclinic.billing.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOSWebhookData {

    private String code;
    private String desc;
    private Data data;
    private String signature;

    @Getter
    @Setter
    public static class Data {
        private Long orderCode;
        private BigDecimal amount;
        private String description;
        private String accountNumber;
        private String reference;
        private String transactionDateTime;
        private String currency;
        private String paymentLinkId;
        private String code;
        private String desc;
    }
}
