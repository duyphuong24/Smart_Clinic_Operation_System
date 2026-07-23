package com.smartclinic.masterdata.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.masterdata.dto.RoomRequest;
import com.smartclinic.masterdata.dto.RoomResponse;
import com.smartclinic.masterdata.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    @GetMapping
    public ApiResponse<List<RoomResponse>> findAll(HttpServletRequest request) {
        return ApiResponse.success("Rooms loaded", roomService.findAll(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("Room loaded", roomService.getById(id), request.getRequestURI());
    }

    @PostMapping
    public ApiResponse<RoomResponse> create(@Valid @RequestBody RoomRequest body, HttpServletRequest request) {
        return ApiResponse.created("Room created", roomService.create(body), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<RoomResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest body,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Room updated", roomService.update(id, body), request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id, HttpServletRequest request) {
        roomService.deactivate(id);
        return ApiResponse.success("Room deactivated", request.getRequestURI());
    }
}