package com.smartclinic.schedule.controller;

import com.smartclinic.audit.service.AuditLogService;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.masterdata.service.SpecialtyService;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class DoctorScheduleBoardController {

    private final DoctorAvailabilityService doctorAvailabilityService;
    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;
    private final RoomService roomService;
    private final AuditLogService auditLogService;

    @GetMapping("/schedule/board")
    public String board(Model model) {
        List<DoctorAvailabilityResponse> availabilities = doctorAvailabilityService.findAll(null);
        model.addAttribute("availabilities", availabilities);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("newShift", new DoctorAvailabilityRequest());
        model.addAttribute("title", "Doctor Duty Schedule Board");
        return "schedule/board";
    }

    @PostMapping("/schedule/board/save")
    public String saveShift(@Valid @ModelAttribute("newShift") DoctorAvailabilityRequest request) {
        DoctorAvailabilityResponse response = doctorAvailabilityService.create(request);
        Long id = (response != null) ? response.getId() : null;
        String doctorName = (response != null) ? response.getDoctorName() : "Doctor";
        Integer dayOfWeek = (response != null) ? response.getDayOfWeek() : request.getDayOfWeek();
        auditLogService.record("CREATE_SHIFT", "DOCTOR_AVAILABILITY", id, "Assigned doctor shift for " + doctorName + " on day " + dayOfWeek);
        return "redirect:/schedule/board";
    }

    @PostMapping("/schedule/board/{id}/delete")
    public String deleteShift(@PathVariable Long id) {
        doctorAvailabilityService.deactivate(id);
        auditLogService.record("REMOVE_SHIFT", "DOCTOR_AVAILABILITY", id, "Deactivated doctor duty shift ID: " + id);
        return "redirect:/schedule/board";
    }
}
