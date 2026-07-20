package com.smartclinic.desktop.session;

import com.smartclinic.desktop.dto.LoginResponse;
import com.smartclinic.desktop.token.TokenStore;
import java.util.List;
import java.util.Optional;

public class SessionManager {

    private final TokenStore tokenStore = new TokenStore();
    private String tokenType;
    private String userName;
    private String fullName;
    private List<String> roles = List.of();

    public void startSession(LoginResponse response) {
        tokenStore.setAccessToken(response.getAccessToken());
        tokenStore.setRefreshToken(response.getRefreshToken());
        this.tokenType = response.getTokenType();
        this.userName = response.getUserName();
        this.fullName = response.getFullName();
        this.roles = response.getRoles() == null ? List.of() : List.copyOf(response.getRoles());
    }

    public void clear() {
        tokenStore.clear();
        this.tokenType = null;
        this.userName = null;
        this.fullName = null;
        this.roles = List.of();
    }

    public boolean isAuthenticated() {
        String access = tokenStore.getAccessToken();
        return access != null && !access.isBlank();
    }

    public Optional<String> authorizationHeader() {
        if (!isAuthenticated()) {
            return Optional.empty();
        }
        String type = tokenType == null || tokenType.isBlank() ? "Bearer" : tokenType;
        return Optional.of(type + " " + tokenStore.getAccessToken());
    }

    public String getAccessToken() {
        return tokenStore.getAccessToken();
    }

    public String getRefreshToken() {
        return tokenStore.getRefreshToken();
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