package com.smartclinic.doctor.controller;

import com.smartclinic.doctor.dto.DoctorRequest;
import com.smartclinic.doctor.dto.DoctorResponse;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.masterdata.service.SpecialtyService;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
import com.smartclinic.staff.service.StaffService;
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
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final StaffService staffService;
    private final SpecialtyService specialtyService;
    private final RoomService roomService;
    private final DoctorAvailabilityService doctorAvailabilityService;

    @GetMapping
    public String list(Model model) {
        List<DoctorResponse> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("title", "Doctors");
        return "doctor/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("doctor", new DoctorRequest());
        model.addAttribute("staffs", staffService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("title", "Doctors");
        return "doctor/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("doctor") DoctorRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("staffs", staffService.findAll());
            model.addAttribute("specialties", specialtyService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Doctors");
            return "doctor/form";
        }
        doctorService.create(request);
        return "redirect:/admin/doctors";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getById(id);
        List<DoctorAvailabilityResponse> availabilities = doctorAvailabilityService.findAll(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("title", "Doctors");
        return "doctor/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getById(id);
        DoctorRequest request = new DoctorRequest();
        request.setStaffId(doctor.getStaffId());
        request.setSpecialtyId(doctor.getSpecialtyId());
        request.setDefaultRoomId(doctor.getDefaultRoomId());
        request.setLicenseNo(doctor.getLicenseNo());
        request.setConsultationFee(doctor.getConsultationFee());
        request.setBio(doctor.getBio());
        request.setActive(doctor.isActive());

        model.addAttribute("doctor", request);
        model.addAttribute("doctorId", id);
        model.addAttribute("staffs", staffService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("title", "Doctors");
        return "doctor/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("doctor") DoctorRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctorId", id);
            model.addAttribute("staffs", staffService.findAll());
            model.addAttribute("specialties", specialtyService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Doctors");
            return "doctor/form";
        }
        doctorService.update(id, request);
        return "redirect:/admin/doctors";
    }
}
