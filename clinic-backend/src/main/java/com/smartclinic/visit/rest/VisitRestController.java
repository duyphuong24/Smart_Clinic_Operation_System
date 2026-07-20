package com.smartclinic.visit.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.visit.dto.VisitCreateRequest;
import com.smartclinic.visit.dto.VisitResponse;
import com.smartclinic.visit.service.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visits")
@RequiredArgsConstructor
public class VisitRestController {

    private final VisitService visitService;

    @GetMapping("/{id}")
    public ApiResponse<VisitResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Visit loaded", visitService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<VisitResponse> create(@Valid @RequestBody VisitCreateRequest body, HttpServletRequest request) {
        return ApiResponse.created("Visit created", visitService.create(body), request.getRequestURI());
    }
}