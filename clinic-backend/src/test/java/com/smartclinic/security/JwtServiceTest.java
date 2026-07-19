package com.smartclinic.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

    private static final String SECRET = "dGVzdC1zaWduZXIta2V5LXNob3VsZC1iZS1hdC1sZWFzdC0yNTYtYml0cy1sb25nLWZvci1zaWduaW5nLXVzZQ==";
    private static final String OTHER_SECRET = "b3RoZXItc2lnbmVyLWtleS1zaG91bGQtYmUtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZy1mb3Itc2lnbmluZy11c2U=";

    @Test
    void generateTokenShouldExposeSubjectAndRoles() {
        JwtService jwtService = new JwtService(SECRET, 60);
        UserDetails userDetails = userDetails();

        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUserName(token)).isEqualTo("admin");
        assertThat(jwtService.extractRoles(token)).containsExactly("ROLE_ADMIN", "ROLE_MANAGER");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void validateTokenShouldRejectTokenSignedWithDifferentSecret() {
        JwtService jwtService = new JwtService(SECRET, 60);
        JwtService otherJwtService = new JwtService(OTHER_SECRET, 60);
        String token = otherJwtService.generateToken(userDetails());

        assertThat(jwtService.isTokenValid(token, userDetails())).isFalse();
    }

    @Test
    void validateTokenShouldRejectExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, -1);
        UserDetails userDetails = userDetails();
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    private UserDetails userDetails() {
        return new User(
                "admin",
                "encoded-password",
                List.of(
                        new SimpleGrantedAuthority("ROLE_MANAGER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );
    }
}