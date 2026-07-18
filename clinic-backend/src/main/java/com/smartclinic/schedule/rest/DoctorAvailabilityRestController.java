package com.smartclinic.schedule.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.schedule.dto.DoctorAvailabilityRequest;
import com.smartclinic.schedule.dto.DoctorAvailabilityResponse;
import com.smartclinic.schedule.service.DoctorAvailabilityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor-availabilities")
@RequiredArgsConstructor
public class DoctorAvailabilityRestController {

    private final DoctorAvailabilityService availabilityService;

    @GetMapping
    public ApiResponse<List<DoctorAvailabilityResponse>> findAll(
            @RequestParam(required = false) Long doctorId,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Doctor availabilities loaded", availabilityService.findAll(doctorId), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<DoctorAvailabilityResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Doctor availability loaded", availabilityService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<DoctorAvailabilityResponse> create(
            @Valid @RequestBody DoctorAvailabilityRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Doctor availability created", availabilityService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<DoctorAvailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DoctorAvailabilityRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Doctor availability updated", availabilityService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        availabilityService.deactivate(id);
        return ApiResponse.success("Doctor availability deactivated", request.getRequestURI());
    }
}