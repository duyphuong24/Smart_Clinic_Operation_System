package com.smartclinic.appointment.rest;

import com.smartclinic.appointment.dto.AppointmentRequest;
import com.smartclinic.appointment.dto.AppointmentRescheduleRequest;
import com.smartclinic.appointment.dto.AppointmentResponse;
import com.smartclinic.appointment.dto.CancelAppointmentRequest;
import com.smartclinic.appointment.service.AppointmentService;
import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.service.QueueItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentRestController {

    private final AppointmentService appointmentService;

    private final QueueItemService queueItemService;

    @GetMapping
    public ApiResponse<List<AppointmentResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Appointments loaded", appointmentService.findAll(date), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Appointment loaded", appointmentService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<AppointmentResponse> create(
            @Valid @RequestBody AppointmentRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Appointment booked", appointmentService.create(body), request.getRequestURI());
    }

    @PostMapping("/{id}/check-in")
    public ApiResponse<QueueItemResponse> checkIn(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        AppointmentCheckInRequest checkInRequest = new AppointmentCheckInRequest();
        checkInRequest.setAppointmentId(id);
        return ApiResponse.created("Appointment checked in", queueItemService.checkIn(checkInRequest), request.getRequestURI());
    }

    @PatchMapping("/{id}/reschedule")
    public ApiResponse<AppointmentResponse> reschedule(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRescheduleRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Appointment rescheduled", appointmentService.reschedule(id, body), request.getRequestURI());
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<AppointmentResponse> cancel(
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Appointment cancelled", appointmentService.cancel(id, body.getReason()), request.getRequestURI());
    }

    @PatchMapping("/{id}/no-show")
    public ApiResponse<AppointmentResponse> markNoShow(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Appointment marked as no-show", appointmentService.markNoShow(id), request.getRequestURI());
    }
}