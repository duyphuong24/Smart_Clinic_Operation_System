package com.smartclinic.billing.controller;

import com.smartclinic.encounter.entity.Encounter;
import com.smartclinic.encounter.entity.EncounterStatus;
import com.smartclinic.encounter.repository.EncounterRepository;
import com.smartclinic.invoice.dto.InvoiceCancelRequest;
import com.smartclinic.invoice.dto.InvoiceCreateRequest;
import com.smartclinic.invoice.dto.InvoiceResponse;
import com.smartclinic.invoice.repository.InvoiceRepository;
import com.smartclinic.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final InvoiceService invoiceService;
    private final EncounterRepository encounterRepository;
    private final InvoiceRepository invoiceRepository;

    @GetMapping
    public String dashboard(Model model) {
        // Fetch completed encounters without invoices (Pending Billing)
        List<Encounter> pendingBillings = encounterRepository.findAll().stream()
                .filter(e -> e.getStatus() == EncounterStatus.COMPLETED)
                .filter(e -> !invoiceRepository.existsByVisitId(e.getVisit().getId()))
                .toList();

        // Fetch all generated invoices (History)
        List<InvoiceResponse> invoiceHistory = invoiceService.findAll(null);

        model.addAttribute("pendingBillings", pendingBillings);
        model.addAttribute("invoiceHistory", invoiceHistory);
        model.addAttribute("title", "Invoices");
        return "billing/dashboard";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam Long encounterId) {
        InvoiceCreateRequest request = new InvoiceCreateRequest();
        request.setEncounterId(encounterId);
        InvoiceResponse response = invoiceService.generate(request);
        return "redirect:/billing/invoices/" + response.getId();
    }

    @GetMapping("/invoices/{id}")
    public String detail(@PathVariable Long id, Model model) {
        InvoiceResponse invoice = invoiceService.getById(id);
        model.addAttribute("invoice", invoice);
        model.addAttribute("title", "Invoices");
        return "billing/detail";
    }

    @PostMapping("/invoices/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        InvoiceCancelRequest request = new InvoiceCancelRequest();
        request.setReason(reason);
        invoiceService.cancel(id, request);
        return "redirect:/billing/invoices/" + id;
    }
}
