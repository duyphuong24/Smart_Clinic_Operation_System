package com.smartclinic.billing.rest;

import com.smartclinic.billing.dto.PayOSWebhookData;
import com.smartclinic.billing.service.PayOSService;
import com.smartclinic.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final PayOSService payOSService;

    @PostMapping("/payos-webhook")
    public ApiResponse<Void> handlePayOSWebhook(@RequestBody PayOSWebhookData webhookData, HttpServletRequest request) {
        log.info("Received PayOS HTTP Webhook POST notification");
        if (!payOSService.verifyWebhookSignature(webhookData)) {
            log.warn("Invalid PayOS webhook signature");
            return ApiResponse.error("Invalid webhook signature", request.getRequestURI());
        }
        payOSService.processWebhook(webhookData);
        return ApiResponse.success("Webhook processed successfully", request.getRequestURI());
    }
}
