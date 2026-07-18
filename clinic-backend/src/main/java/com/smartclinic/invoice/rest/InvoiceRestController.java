package com.smartclinic.invoice.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.invoice.dto.InvoiceCancelRequest;
import com.smartclinic.invoice.dto.InvoiceCreateRequest;
import com.smartclinic.invoice.dto.InvoiceResponse;
import com.smartclinic.invoice.entity.InvoiceStatus;
import com.smartclinic.invoice.service.InvoiceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceRestController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ApiResponse<List<InvoiceResponse>> findAll(
            @RequestParam(required = false) InvoiceStatus status,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Invoices loaded", invoiceService.findAll(status), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<InvoiceResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Invoice loaded", invoiceService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<InvoiceResponse> generate(
            @Valid @RequestBody InvoiceCreateRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Invoice generated", invoiceService.generate(body), request.getRequestURI());
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<InvoiceResponse> cancel(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceCancelRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Invoice cancelled", invoiceService.cancel(id, body), request.getRequestURI());
    }
}