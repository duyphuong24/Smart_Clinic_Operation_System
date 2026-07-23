package com.smartclinic.billing.mapper;

import com.smartclinic.billing.dto.InvoiceItemResponse;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.entity.Invoice;
import com.smartclinic.billing.entity.InvoiceItem;
import java.util.Collections;
import java.util.List;

public final class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceItemResponse toItemResponse(InvoiceItem item) {
        if (item == null) {
            return null;
        }
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

    public static InvoiceResponse toResponse(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        List<InvoiceItemResponse> items = invoice.getItems() != null
                ? invoice.getItems().stream().map(InvoiceMapper::toItemResponse).toList()
                : Collections.emptyList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .visitId(invoice.getVisit() != null ? invoice.getVisit().getId() : null)
                .visitCode(invoice.getVisit() != null ? invoice.getVisit().getVisitCode() : null)
                .encounterId(invoice.getEncounter() != null ? invoice.getEncounter().getId() : null)
                .patientId(invoice.getPatient() != null ? invoice.getPatient().getId() : null)
                .patientCode(invoice.getPatient() != null ? invoice.getPatient().getPatientCode() : null)
                .patientName(invoice.getPatient() != null ? invoice.getPatient().getFullName() : null)
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .cancelledAt(invoice.getCancelledAt())
                .cancelReason(invoice.getCancelReason())
                .items(items)
                .build();
    }
}
