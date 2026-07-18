package com.smartclinic.masterdata.controller;

import com.smartclinic.masterdata.dto.SpecialtyRequest;
import com.smartclinic.masterdata.dto.SpecialtyResponse;
import com.smartclinic.masterdata.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping("/masterdata")
    public String masterDataRedirect() {
        return "redirect:/admin/specialties";
    }

    @GetMapping("/specialties")
    public String list(Model model) {
        List<SpecialtyResponse> specialties = specialtyService.findAll();
        model.addAttribute("specialties", specialties);
        model.addAttribute("title", "Master Data");
        return "admin/specialty-list";
    }

    @GetMapping("/specialties/new")
    public String createForm(Model model) {
        model.addAttribute("specialty", new SpecialtyRequest());
        model.addAttribute("isEdit", false);
        model.addAttribute("title", "Master Data");
        return "admin/specialty-form";
    }

    @PostMapping("/specialties/new")
    public String create(
            @Valid @ModelAttribute("specialty") SpecialtyRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Master Data");
            return "admin/specialty-form";
        }
        specialtyService.create(request);
        return "redirect:/admin/specialties";
    }

    @GetMapping("/specialties/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        SpecialtyResponse specialty = specialtyService.getById(id);
        SpecialtyRequest request = new SpecialtyRequest();
        request.setName(specialty.getName());
        request.setDescription(specialty.getDescription());
        request.setActive(specialty.isActive());

        model.addAttribute("specialty", request);
        model.addAttribute("specialtyId", id);
        model.addAttribute("isEdit", true);
        model.addAttribute("title", "Master Data");
        return "admin/specialty-form";
    }

    @PostMapping("/specialties/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("specialty") SpecialtyRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialtyId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Master Data");
            return "admin/specialty-form";
        }
        specialtyService.update(id, request);
        return "redirect:/admin/specialties";
    }
}
