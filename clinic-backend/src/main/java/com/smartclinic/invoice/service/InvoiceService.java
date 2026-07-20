package com.smartclinic.invoice.service;

import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.invoice.dto.InvoiceCancelRequest;
import com.smartclinic.invoice.dto.InvoiceCreateRequest;
import com.smartclinic.invoice.dto.InvoiceResponse;
import com.smartclinic.invoice.entity.InvoiceStatus;
import java.util.List;

public interface InvoiceService {

    List<InvoiceResponse> findAll(InvoiceStatus status);

    List<Encounter> findPendingBillings();

    InvoiceResponse getById(Long id);

    InvoiceResponse generate(InvoiceCreateRequest request);

    InvoiceResponse cancel(Long id, InvoiceCancelRequest request);
}