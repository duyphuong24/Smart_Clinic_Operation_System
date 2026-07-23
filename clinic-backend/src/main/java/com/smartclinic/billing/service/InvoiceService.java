package com.smartclinic.billing.service;

import com.smartclinic.billing.dto.InvoiceCancelRequest;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.entity.InvoiceStatus;
import com.smartclinic.encounter.entity.Encounter;
import java.util.List;

public interface InvoiceService {

    List<InvoiceResponse> findAll(InvoiceStatus status);

    List<Encounter> findPendingBillings();

    InvoiceResponse getById(Long id);

    InvoiceResponse generate(InvoiceCreateRequest request);

    InvoiceResponse cancel(Long id, InvoiceCancelRequest request);
}
