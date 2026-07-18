package com.smartclinic.encounter.serviceorder.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderRequest;
import com.smartclinic.encounter.serviceorder.dto.EncounterServiceOrderResponse;
import com.smartclinic.encounter.serviceorder.service.EncounterServiceOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EncounterServiceOrderRestController {

    private final EncounterServiceOrderService encounterServiceOrderService;

    @PostMapping("/api/v1/encounters/{encounterId}/services")
    public ApiResponse<EncounterServiceOrderResponse> add(
            @PathVariable Long encounterId,
            @Valid @RequestBody EncounterServiceOrderRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created(
                "Encounter service ordered",
                encounterServiceOrderService.add(encounterId, body),
                request.getRequestURI()
        );
    }

    @GetMapping("/api/v1/encounters/{encounterId}/services")
    public ApiResponse<List<EncounterServiceOrderResponse>> findByEncounter(
            @PathVariable Long encounterId,
            HttpServletRequest request
    ) {
        return ApiResponse.success(
                "Encounter services loaded",
                encounterServiceOrderService.findByEncounter(encounterId),
                request.getRequestURI()
        );
    }

    @PatchMapping("/api/v1/encounter-services/{id}/complete")
    public ApiResponse<EncounterServiceOrderResponse> complete(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success(
                "Encounter service completed",
                encounterServiceOrderService.complete(id),
                request.getRequestURI()
        );
    }

    @PatchMapping("/api/v1/encounter-services/{id}/cancel")
    public ApiResponse<EncounterServiceOrderResponse> cancel(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success(
                "Encounter service cancelled",
                encounterServiceOrderService.cancel(id),
                request.getRequestURI()
        );
    }
}