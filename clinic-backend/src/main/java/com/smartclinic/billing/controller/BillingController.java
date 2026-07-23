package com.smartclinic.billing.controller;

import com.smartclinic.billing.dto.InvoiceCancelRequest;
import com.smartclinic.billing.dto.InvoiceCreateRequest;
import com.smartclinic.billing.dto.InvoiceResponse;
import com.smartclinic.billing.dto.PayOSPaymentRequest;
import com.smartclinic.billing.dto.PayOSPaymentResponse;
import com.smartclinic.billing.dto.PaymentRequest;
import com.smartclinic.billing.dto.PaymentResponse;
import com.smartclinic.billing.entity.PaymentMethod;
import com.smartclinic.billing.entity.PaymentStatus;
import com.smartclinic.billing.service.InvoiceService;
import com.smartclinic.billing.service.PayOSService;
import com.smartclinic.billing.service.PaymentService;
import com.smartclinic.encounter.entity.Encounter;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final PayOSService payOSService;

    @GetMapping
    public String dashboard(Model model) {
        List<Encounter> pendingBillings = invoiceService.findPendingBillings();
        List<InvoiceResponse> invoiceHistory = invoiceService.findAll(null);
        List<PaymentResponse> paymentHistory = paymentService.findAll(null, null);

        model.addAttribute("pendingBillings", pendingBillings);
        model.addAttribute("invoiceHistory", invoiceHistory);
        model.addAttribute("paymentHistory", paymentHistory);
        model.addAttribute("title", "Invoices");
        return "billing/dashboard";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam Long encounterId, RedirectAttributes redirectAttributes) {
        try {
            InvoiceCreateRequest request = new InvoiceCreateRequest();
            request.setEncounterId(encounterId);
            InvoiceResponse response = invoiceService.generate(request);
            redirectAttributes.addFlashAttribute("success", "Invoice generated successfully: " + response.getInvoiceNumber());
            return "redirect:/billing/invoices/" + response.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate invoice: " + ex.getMessage());
            return "redirect:/billing";
        }
    }

    @GetMapping("/invoices/{id}")
    public String detail(@PathVariable Long id, Model model) {
        InvoiceResponse invoice = invoiceService.getById(id);
        List<PaymentResponse> payments = paymentService.findAll(id, null);

        model.addAttribute("invoice", invoice);
        model.addAttribute("payments", payments);
        model.addAttribute("paymentMethods", PaymentMethod.values());

        // Generate PayOS VietQR info for UI modal
        PayOSPaymentResponse payosQr = payOSService.createPaymentLink(PayOSPaymentRequest.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .amount(invoice.getTotalAmount())
                .description("Thanh toan hoa don " + invoice.getInvoiceNumber())
                .build());
        model.addAttribute("payosQr", payosQr);

        model.addAttribute("title", "Invoices");
        return "billing/detail";
    }

    @PostMapping("/invoices/{id}/payments")
    public String recordPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String transactionRef,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes
    ) {
        try {
            PaymentRequest request = new PaymentRequest();
            request.setAmount(amount);
            request.setMethod(method);
            request.setStatus(PaymentStatus.SUCCESS);
            request.setTransactionRef(transactionRef);
            request.setNote(note);

            PaymentResponse payment = paymentService.record(id, request);
            redirectAttributes.addFlashAttribute("success", "Payment of " + payment.getAmount() + " ₫ recorded successfully (" + method + ").");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Failed to record payment: " + ex.getMessage());
        }
        return "redirect:/billing/invoices/" + id;
    }

    @PostMapping("/invoices/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes
    ) {
        if (reason == null || reason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel: Reason for cancellation is required.");
            return "redirect:/billing/invoices/" + id;
        }
        try {
            InvoiceCancelRequest request = new InvoiceCancelRequest();
            request.setReason(reason);
            InvoiceResponse response = invoiceService.cancel(id, request);
            redirectAttributes.addFlashAttribute("success", "Invoice " + response.getInvoiceNumber() + " has been cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel invoice: " + ex.getMessage());
        }
        return "redirect:/billing/invoices/" + id;
    }
}
