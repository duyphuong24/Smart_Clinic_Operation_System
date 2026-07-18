package com.smartclinic.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaceholderController {

    @GetMapping("/billing")
    public String billingPlaceholder(Model model) {
        model.addAttribute("title", "Invoices");
        model.addAttribute("moduleName", "Billing & Cashier Desk");
        model.addAttribute("description", "This module manages billing, invoicing, and payment processing for completed consultations.");
        model.addAttribute("details", "According to the system architecture, billing and payments are processed directly by Cashiers at the front desk using the JavaFX Desktop Client application to ensure quick transactions.");
        model.addAttribute("icon", "fa-file-invoice-dollar text-warning");
        return "error/placeholder";
    }

    @GetMapping("/admin/staff")
    public String staffPlaceholder(Model model) {
        model.addAttribute("title", "Staff");
        model.addAttribute("moduleName", "Staff Administration");
        model.addAttribute("description", "Manage employee records, allocate roles, and view login activities.");
        model.addAttribute("details", "Staff provisioning, accounts creation, and security role configurations (receptionists, doctors, cashiers, managers) are handled by the system administrator.");
        model.addAttribute("icon", "fa-users-gear text-primary");
        return "error/placeholder";
    }
}
