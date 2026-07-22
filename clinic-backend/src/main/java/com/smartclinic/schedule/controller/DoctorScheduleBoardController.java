package com.smartclinic.schedule.controller;

import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.SpecialtyService;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DoctorScheduleBoardController {

    private final DoctorAvailabilityService doctorAvailabilityService;
    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;

    @GetMapping("/schedule/board")
    public String board(Model model) {
        List<DoctorAvailabilityResponse> availabilities = doctorAvailabilityService.findAll(null);
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("title", "Doctor Duty Schedule Board");
        return "schedule/board";
    }
}
