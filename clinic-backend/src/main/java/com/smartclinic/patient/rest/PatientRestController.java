package com.smartclinic.patient.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.common.api.PageResponse;
import com.smartclinic.patient.dto.PatientCreateRequest;
import com.smartclinic.patient.dto.PatientMedicalHistoryResponse;
import com.smartclinic.patient.dto.PatientResponse;
import com.smartclinic.patient.dto.PatientUpdateRequest;
import com.smartclinic.patient.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientRestController {

    private final PatientService patientService;

    @GetMapping
    public ApiResponse<PageResponse<PatientResponse>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<PatientResponse> response = patientService.search(keyword, pageable);
        return ApiResponse.success("Patients loaded", response, request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<PatientResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Patient loaded", patientService.getById(id), request.getRequestURI());
    }

    @GetMapping("/{id}/medical-history")
    public ApiResponse<PatientMedicalHistoryResponse> getMedicalHistory(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Patient medical history loaded", patientService.getMedicalHistory(id), request.getRequestURI());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PatientResponse> create(
            @Valid @RequestBody PatientCreateRequest createRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Patient created", patientService.create(createRequest), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Patient updated", patientService.update(id, updateRequest), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        patientService.deactivate(id);
        return ApiResponse.success("Patient deactivated", request.getRequestURI());
    }
}