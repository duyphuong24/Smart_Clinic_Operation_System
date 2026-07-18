package com.smartclinic.patient.controller;

import com.smartclinic.common.api.PageResponse;
import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.dto.PatientUpdateRequest;
import com.smartclinic.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<PatientResponse> patients = patientService.search(keyword, pageable);

        model.addAttribute("patients", patients.getItems());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patients.getTotalPages());
        model.addAttribute("totalItems", patients.getTotalItems());
        model.addAttribute("title", "Patients");

        return "patient/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("patient", new PatientCreateRequest());
        model.addAttribute("isEdit", false);
        model.addAttribute("title", "Patients");
        return "patient/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("patient") PatientCreateRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Patients");
            return "patient/form";
        }
        patientService.create(request);
        return "redirect:/patients";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PatientResponse patient = patientService.getById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("title", "Patients");
        return "patient/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PatientResponse patient = patientService.getById(id);
        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setFullName(patient.getFullName());
        request.setDateOfBirth(patient.getDateOfBirth());
        request.setGender(patient.getGender());
        request.setPhone(patient.getPhone());
        request.setEmail(patient.getEmail());
        request.setAddress(patient.getAddress());
        request.setIdentityNumber(patient.getIdentityNumber());
        request.setEmergencyContactName(patient.getEmergencyContactName());
        request.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        request.setAllergyNote(patient.getAllergyNote());

        model.addAttribute("patient", request);
        model.addAttribute("patientId", id);
        model.addAttribute("isEdit", true);
        model.addAttribute("title", "Patients");
        return "patient/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("patient") PatientUpdateRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patientId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Patients");
            return "patient/form";
        }
        patientService.update(id, request);
        return "redirect:/patients";
    }
}
