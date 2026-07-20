package com.smartclinic.appointment.controller;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.dto.AppointmentResponse;
import com.smartclinic.appointment.entity.AppointmentSource;
import com.smartclinic.appointment.service.AppointmentService;
import com.smartclinic.doctor.service.DoctorService;
import com.smartclinic.masterdata.service.RoomService;
import com.smartclinic.patient.service.PatientService;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.entity.QueuePriority;
import com.smartclinic.queue.service.QueueItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final RoomService roomService;
    private final QueueItemService queueItemService;

    @GetMapping
    public String list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<AppointmentResponse> appointments = appointmentService.findAll(date);

        model.addAttribute("appointments", appointments);
        model.addAttribute("selectedDate", date);
        model.addAttribute("title", "Appointments");
        return "appointment/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("appointment", new AppointmentRequest());
        model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("sources", AppointmentSource.values());
        model.addAttribute("title", "Appointments");
        return "appointment/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("appointment") AppointmentRequest request,
            BindingResult bindingResult,
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("sources", AppointmentSource.values());
            model.addAttribute("title", "Appointments");
            return "appointment/form";
        }
        try {
            appointmentService.create(request);
            redirectAttributes.addFlashAttribute("success", "Appointment booked successfully.");
            return "redirect:/appointments";
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("patients", patientService.search("", PageRequest.of(0, 100)).getItems());
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("rooms", roomService.findAll());
            model.addAttribute("sources", AppointmentSource.values());
            model.addAttribute("title", "Appointments");
            return "appointment/form";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Cancelled by receptionist/patient request") String reason,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        try {
            appointmentService.cancel(id, reason);
            redirectAttributes.addFlashAttribute("success", "Appointment cancelled successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/check-in")
    public String checkIn(
            @PathVariable Long id,
            @RequestParam(defaultValue = "NORMAL") QueuePriority priority,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes
    ) {
        try {
            AppointmentCheckInRequest checkInRequest = new AppointmentCheckInRequest();
            checkInRequest.setAppointmentId(id);
            checkInRequest.setPriority(priority);
            queueItemService.checkIn(checkInRequest);
            redirectAttributes.addFlashAttribute("success", "Checked in patient successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/appointments";
    }
}
