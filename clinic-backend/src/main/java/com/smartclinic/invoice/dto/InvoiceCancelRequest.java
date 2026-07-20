package com.smartclinic.invoice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceCancelRequest {

    @Size(max = 500)
    private String reason;
}