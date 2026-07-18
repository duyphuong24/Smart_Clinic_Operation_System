package com.smartclinic.schedule.controller;

import com.smartclinic.doctor.dto.DoctorResponse;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
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

@Controller
@RequestMapping("/admin/doctors/{id}/availability")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;
    private final DoctorService doctorService;
    private final RoomService roomService;

    @GetMapping("/new")
    public String createForm(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getById(id);
        DoctorAvailabilityRequest request = new DoctorAvailabilityRequest();
        request.setDoctorId(id);

        model.addAttribute("availability", request);
        model.addAttribute("doctor", doctor);
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("title", "Doctors");
        return "schedule/availability-form";
    }

    @PostMapping("/new")
    public String create(
            @PathVariable Long id,
            @Valid @ModelAttribute("availability") DoctorAvailabilityRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            DoctorResponse doctor = doctorService.getById(id);
            model.addAttribute("doctor", doctor);
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("title", "Doctors");
            return "schedule/availability-form";
        }
        request.setDoctorId(id);
        doctorAvailabilityService.create(request);
        return "redirect:/admin/doctors/" + id;
    }

    @PostMapping("/{availId}/deactivate")
    public String deactivate(
            @PathVariable Long id,
            @PathVariable Long availId
    ) {
        doctorAvailabilityService.deactivate(availId);
        return "redirect:/admin/doctors/" + id;
    }
}
