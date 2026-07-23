package com.smartclinic.staff.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.staff.dto.StaffRequest;
import com.smartclinic.staff.dto.StaffResponse;
import com.smartclinic.staff.service.StaffService;
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
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffRestController {

    private final StaffService staffService;

    @GetMapping
    public ApiResponse<List<StaffResponse>> findAll(HttpServletRequest request) {
        return ApiResponse.success("Staff loaded", staffService.findAll(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Staff loaded", staffService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<StaffResponse> create(@Valid @RequestBody StaffRequest body, HttpServletRequest request) {
        return ApiResponse.created("Staff created", staffService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<StaffResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StaffRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Staff updated", staffService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        staffService.deactivate(id);
        return ApiResponse.success("Staff deactivated", request.getRequestURI());
    }
}