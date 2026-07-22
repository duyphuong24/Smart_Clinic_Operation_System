package com.smartclinic.servicecatalog.controller;

import com.smartclinic.servicecatalog.dto.ServiceCatalogRequest;
import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.service.ServiceCatalogService;
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

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public String list(@RequestParam(required = false) ServiceType type, Model model) {
        List<ServiceCatalogResponse> services = serviceCatalogService.findAll(type, false);
        model.addAttribute("services", services);
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("title", "Medical Services Catalog");
        return "admin/service-list";
    }

    @PostMapping("/save")
    public String saveService(
            @RequestParam String serviceCode,
            @RequestParam String name,
            @RequestParam ServiceType type,
            @RequestParam BigDecimal price
    ) {
        ServiceCatalogRequest request = new ServiceCatalogRequest();
        request.setServiceCode(serviceCode);
        request.setName(name);
        request.setType(type);
        request.setPrice(price);
        request.setActive(true);

        serviceCatalogService.create(request);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        serviceCatalogService.deactivate(id);
        return "redirect:/admin/services";
    }
}
