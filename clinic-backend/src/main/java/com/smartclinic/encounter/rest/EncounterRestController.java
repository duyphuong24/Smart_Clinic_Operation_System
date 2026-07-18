package com.smartclinic.encounter.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.encounter.dto.EncounterCreateRequest;
import com.smartclinic.encounter.dto.EncounterResponse;
import com.smartclinic.encounter.dto.EncounterUpdateRequest;
import com.smartclinic.encounter.service.EncounterWorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/encounters")
@RequiredArgsConstructor
public class EncounterRestController {

    private final EncounterWorkflowService encounterWorkflowService;

    @GetMapping("/{id}")
    public ApiResponse<EncounterResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Encounter loaded", encounterWorkflowService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<EncounterResponse> create(
            @Valid @RequestBody EncounterCreateRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Encounter opened", encounterWorkflowService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<EncounterResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EncounterUpdateRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Encounter updated", encounterWorkflowService.update(id, body), request.getRequestURI());
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<EncounterResponse> complete(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Encounter completed", encounterWorkflowService.complete(id), request.getRequestURI());
    }
}