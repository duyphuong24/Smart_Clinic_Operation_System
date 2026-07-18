package com.smartclinic.invoice.dto;

import com.smartclinic.invoice.entity.InvoiceItemType;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceItemResponse {

    private final Long id;
    private final InvoiceItemType itemType;
    private final Long referenceId;
    private final String description;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
}