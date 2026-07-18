package com.smartclinic.masterdata.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.masterdata.dto.SpecialtyRequest;
import com.smartclinic.masterdata.dto.SpecialtyResponse;
import com.smartclinic.masterdata.service.SpecialtyService;
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
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
public class SpecialtyRestController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ApiResponse<List<SpecialtyResponse>> findAll(HttpServletRequest request) {
        return ApiResponse.success("Specialties loaded", specialtyService.findAll(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<SpecialtyResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Specialty loaded", specialtyService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<SpecialtyResponse> create(@Valid @RequestBody SpecialtyRequest body, HttpServletRequest request) {
        return ApiResponse.created("Specialty created", specialtyService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<SpecialtyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SpecialtyRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Specialty updated", specialtyService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        specialtyService.deactivate(id);
        return ApiResponse.success("Specialty deactivated", request.getRequestURI());
    }
}