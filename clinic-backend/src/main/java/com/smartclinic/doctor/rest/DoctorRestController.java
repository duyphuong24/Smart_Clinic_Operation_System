package com.smartclinic.doctor.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.doctor.dto.DoctorRequest;
import com.smartclinic.doctor.dto.DoctorResponse;
import com.smartclinic.doctor.service.DoctorService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorRestController {

    private final DoctorService doctorService;

    @GetMapping
    public ApiResponse<List<DoctorResponse>> findAll(HttpServletRequest request) {
        return ApiResponse.success("Doctors loaded", doctorService.findAll(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<DoctorResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Doctor loaded", doctorService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<DoctorResponse> create(@Valid @RequestBody DoctorRequest body, HttpServletRequest request) {
        return ApiResponse.created("Doctor created", doctorService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<DoctorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Doctor updated", doctorService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        doctorService.deactivate(id);
        return ApiResponse.success("Doctor deactivated", request.getRequestURI());
    }
}