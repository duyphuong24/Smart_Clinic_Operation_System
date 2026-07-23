package com.smartclinic.billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceCreateRequest {

    @NotNull
    private Long encounterId;
}
