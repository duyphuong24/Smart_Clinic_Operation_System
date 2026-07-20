package com.smartclinic.invoice.mapper;

import com.smartclinic.invoice.dto.InvoiceItemResponse;
import com.smartclinic.invoice.dto.InvoiceResponse;
import com.smartclinic.invoice.entity.Invoice;
import com.smartclinic.invoice.entity.InvoiceItem;

public final class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .visitId(invoice.getVisit().getId())
                .visitCode(invoice.getVisit().getVisitCode())
                .encounterId(invoice.getEncounter().getId())
                .patientId(invoice.getPatient().getId())
                .patientCode(invoice.getPatient().getPatientCode())
                .patientName(invoice.getPatient().getFullName())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .cancelledAt(invoice.getCancelledAt())
                .cancelReason(invoice.getCancelReason())
                .items(invoice.getItems().stream().map(InvoiceMapper::toItemResponse).toList())
                .build();
    }

    private static InvoiceItemResponse toItemResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .itemType(item.getItemType())
                .referenceId(item.getReferenceId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}