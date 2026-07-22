package com.smartclinic.user.rest;

import com.smartclinic.common.api.ApiResponse;
import com.smartclinic.common.api.PageResponse;
import com.smartclinic.user.dto.ResetPasswordRequest;
import com.smartclinic.user.dto.UserCreateRequest;
import com.smartclinic.user.dto.UserResponse;
import com.smartclinic.user.dto.UserStatusUpdateRequest;
import com.smartclinic.user.dto.UserUpdateRequest;
import com.smartclinic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> search(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<UserResponse> response = userService.search(keyword, pageable);
        return ApiResponse.success("Users loaded", response, request.getRequestURI());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success("User loaded", userService.getById(id), request.getRequestURI());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> create(
            @Valid @RequestBody UserCreateRequest createRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.created("User created", userService.create(createRequest), request.getRequestURI());
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.success("User updated", userService.update(id, updateRequest), request.getRequestURI());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest statusRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.success("User status updated", userService.updateStatus(id, statusRequest), request.getRequestURI());
    }

    @PatchMapping("/{id}/reset-password")
    public ApiResponse<UserResponse> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
            HttpServletRequest request
    ) {
        return ApiResponse.success("Password reset successfully", userService.resetPassword(id, resetPasswordRequest), request.getRequestURI());
    }
}
