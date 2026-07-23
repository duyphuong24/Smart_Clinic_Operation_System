package com.smartclinic.patient.controller;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.service.AppointmentService;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.SpecialtyService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my-portal")
@RequiredArgsConstructor
public class PatientPortalController {

    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;
    private final AppointmentService appointmentService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("title", "Patient Self-Service Portal");
        return "patient/portal";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(
            @RequestParam Long doctorId,
            @RequestParam String scheduledStart,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LocalDateTime start = LocalDateTime.parse(scheduledStart);
            LocalDateTime end = start.plusMinutes(30);

            AppointmentRequest request = new AppointmentRequest();
            request.setDoctorId(doctorId);
            request.setPatientId(1L);
            request.setScheduledStart(start);
            request.setScheduledEnd(end);
            request.setReason(reason);

            var appointment = appointmentService.create(request);
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully! Code: " + appointment.getAppointmentCode());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Failed to book appointment: " + ex.getMessage());
        }
        return "redirect:/my-portal";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("appointments", appointmentService.findAll(null));
        model.addAttribute("title", "My Medical History & Records");
        return "patient/history";
    }
}
