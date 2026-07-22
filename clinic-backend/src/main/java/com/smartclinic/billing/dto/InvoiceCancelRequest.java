package com.smartclinic.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceCancelRequest {

    @NotBlank
    @Size(max = 500)
    private String reason;
}
