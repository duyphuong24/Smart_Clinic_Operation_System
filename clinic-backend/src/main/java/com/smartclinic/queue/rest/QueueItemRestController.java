package com.smartclinic.queue.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.queue.dto.AppointmentCheckInRequest;
import com.smartclinic.queue.dto.QueueItemResponse;
import com.smartclinic.queue.dto.WalkInQueueRequest;
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
@RequestMapping("/api/v1/queue-items")
@RequiredArgsConstructor
public class QueueItemRestController {

    private final QueueItemService queueItemService;

    @GetMapping
    public ApiResponse<List<QueueItemResponse>> findActive(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Queue items loaded", queueItemService.findActive(date, doctorId), request.getRequestURI());
    }

    @PostMapping("/check-in")
    public ApiResponse<QueueItemResponse> checkIn(
            @Valid @RequestBody AppointmentCheckInRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Appointment checked in", queueItemService.checkIn(body), request.getRequestURI());
    }

    @PostMapping("/walk-in")
    public ApiResponse<QueueItemResponse> createWalkIn(
            @Valid @RequestBody WalkInQueueRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.created("Walk-in queue item created", queueItemService.createWalkIn(body), request.getRequestURI());
    }

    @PatchMapping("/{id}/call")
    public ApiResponse<QueueItemResponse> call(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Queue item called", queueItemService.call(id), request.getRequestURI());
    }

    @PatchMapping("/{id}/start-service")
    public ApiResponse<QueueItemResponse> startService(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Queue item started", queueItemService.startService(id), request.getRequestURI());
    }

    @PatchMapping("/{id}/done")
    public ApiResponse<QueueItemResponse> done(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Queue item done", queueItemService.done(id), request.getRequestURI());
    }

    @PatchMapping("/{id}/skip")
    public ApiResponse<QueueItemResponse> skip(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Queue item skipped", queueItemService.skip(id), request.getRequestURI());
    }
}