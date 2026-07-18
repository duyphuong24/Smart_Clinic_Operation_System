package com.smartclinic.servicecatalog.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.servicecatalog.dto.ServiceCatalogRequest;
import com.smartclinic.servicecatalog.dto.ServiceCatalogResponse;
import com.smartclinic.servicecatalog.entity.ServiceType;
import com.smartclinic.servicecatalog.service.ServiceCatalogService;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceCatalogRestController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ApiResponse<List<ServiceCatalogResponse>> findAll(
            @RequestParam(required = false) ServiceType type,
            @RequestParam(required = false) Boolean activeOnly,
            HttpServletRequest request
    ) {
        return ApiResponse.success(
                "Services loaded",
                serviceCatalogService.findAll(type, activeOnly),
                request.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceCatalogResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Service loaded", serviceCatalogService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<ServiceCatalogResponse> create(
            @Valid @RequestBody ServiceCatalogRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Service created", serviceCatalogService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceCatalogResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ServiceCatalogRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Service updated", serviceCatalogService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        serviceCatalogService.deactivate(id);
        return ApiResponse.success("Service deactivated", request.getRequestURI());
    }
}