package com.smartclinic.billing.service;

import com.smartclinic.billing.dto.PayOSPaymentRequest;
import com.smartclinic.billing.dto.PayOSPaymentResponse;
import com.smartclinic.billing.dto.PayOSWebhookData;

public interface PayOSService {

    PayOSPaymentResponse createPaymentLink(PayOSPaymentRequest request);

    boolean verifyWebhookSignature(PayOSWebhookData webhookData);

    void processWebhook(PayOSWebhookData webhookData);
}
