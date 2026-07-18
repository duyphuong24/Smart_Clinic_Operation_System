package com.smartclinic.payment.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.payment.dto.PaymentRequest;
import com.smartclinic.payment.dto.PaymentResponse;
import com.smartclinic.payment.entity.PaymentStatus;
import com.smartclinic.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/api/v1/invoices/{invoiceId}/payments")
    public ApiResponse<PaymentResponse> record(
            @PathVariable Long invoiceId,
            @Valid @RequestBody PaymentRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Payment recorded", paymentService.record(invoiceId, body), request.getRequestURI());
    }

    @GetMapping("/api/v1/payments")
    public ApiResponse<List<PaymentResponse>> findAll(
            @RequestParam(required = false) Long invoiceId,
            @RequestParam(required = false) PaymentStatus status,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Payments loaded", paymentService.findAll(invoiceId, status), request.getRequestURI());
    }
}