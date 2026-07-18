package com.smartclinic.auth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final String accessToken;
    private final String tokenType;
    private final String userName;
    private final String fullName;
    private final List<String> roles;
}