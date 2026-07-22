package com.smartclinic.servicecatalog.controller;

import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.service.ServiceCatalogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public String list(@RequestParam(required = false) ServiceType type, Model model) {
        List<ServiceCatalogResponse> services = serviceCatalogService.findAll(type, true);
        model.addAttribute("services", services);
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("title", "Medical Services Catalog");
        return "admin/service-list";
    }
}
