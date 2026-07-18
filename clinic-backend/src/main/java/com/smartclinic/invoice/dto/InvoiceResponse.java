package com.smartclinic.invoice.dto;

import com.smartclinic.invoice.entity.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceResponse {

    private final Long id;
    private final String invoiceNumber;
    private final Long visitId;
    private final String visitCode;
    private final Long encounterId;
    private final Long patientId;
    private final String patientCode;
    private final String patientName;
    private final BigDecimal totalAmount;
    private final InvoiceStatus status;
    private final LocalDateTime issuedAt;
    private final LocalDateTime cancelledAt;
    private final String cancelReason;
    private final List<InvoiceItemResponse> items;
}