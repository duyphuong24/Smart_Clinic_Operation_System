package com.smartclinic.desktop.session;

import com.smartclinic.desktop.dto.LoginResponse;
import java.util.List;
import java.util.Optional;

public class SessionManager {

    private String accessToken;
    private String tokenType;
    private String userName;
    private String fullName;
    private List<String> roles = List.of();

    public void startSession(LoginResponse response) {
        this.accessToken = response.getAccessToken();
        this.tokenType = response.getTokenType();
        this.userName = response.getUserName();
        this.fullName = response.getFullName();
        this.roles = response.getRoles() == null ? List.of() : List.copyOf(response.getRoles());
    }

    public void clear() {
        this.accessToken = null;
        this.tokenType = null;
        this.userName = null;
        this.fullName = null;
        this.roles = List.of();
    }

    public boolean isAuthenticated() {
        return accessToken != null && !accessToken.isBlank();
    }

    public Optional<String> authorizationHeader() {
        if (!isAuthenticated()) {
            return Optional.empty();
        }
        String type = tokenType == null || tokenType.isBlank() ? "Bearer" : tokenType;
        return Optional.of(type + " " + accessToken);
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getRoles() {
        return roles;
    }
}