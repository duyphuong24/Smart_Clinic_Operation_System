package com.smartclinic.payment.dto;

import com.smartclinic.payment.entity.PaymentMethod;
import com.smartclinic.payment.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    private PaymentStatus status = PaymentStatus.SUCCESS;

    @Size(max = 100)
    private String transactionRef;

    @Size(max = 500)
    private String note;
}