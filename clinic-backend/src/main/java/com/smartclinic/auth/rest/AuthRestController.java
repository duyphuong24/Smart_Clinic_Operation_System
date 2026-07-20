package com.smartclinic.auth.rest;

import com.smartclinic.auth.dto.CurrentUserResponse;
import com.smartclinic.auth.dto.LoginRequest;
import com.smartclinic.auth.dto.LoginResponse;
import com.smartclinic.auth.dto.TokenRefreshRequest;
import com.smartclinic.auth.service.AuthService;
import com.smartclinic.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response, servletRequest.getRequestURI());
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @Valid @RequestBody TokenRefreshRequest request,
            HttpServletRequest servletRequest
    ) {
        LoginResponse response = authService.refresh(request);
        return ApiResponse.success("Token refreshed successfully", response, servletRequest.getRequestURI());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestBody(required = false) TokenRefreshRequest request,
            HttpServletRequest servletRequest
    ) {
        if (request != null) {
            authService.logout(request.getRefreshToken());
        }
        return ApiResponse.success("Logout successful", servletRequest.getRequestURI());
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        CurrentUserResponse response = authService.currentUser(authentication);
        return ApiResponse.success("Current user loaded", response, servletRequest.getRequestURI());
    }
}