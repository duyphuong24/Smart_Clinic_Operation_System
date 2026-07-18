package com.smartclinic.auth.service;

import com.smartclinic.auth.dto.CurrentUserResponse;
import com.smartclinic.auth.dto.LoginRequest;
import com.smartclinic.auth.dto.LoginResponse;
import com.smartclinic.auth.dto.TokenRefreshRequest;
import com.smartclinic.auth.entity.RefreshToken;
import com.smartclinic.auth.repository.RefreshTokenRepository;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.security.JwtService;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long accessTokenValidSeconds;
    private final long refreshTokenValidSeconds;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${smartclinic.jwt.access-token-valid-seconds}") long accessTokenValidSeconds,
            @Value("${smartclinic.jwt.refresh-token-valid-seconds}") long refreshTokenValidSeconds
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenValidSeconds = accessTokenValidSeconds;
        this.refreshTokenValidSeconds = refreshTokenValidSeconds;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = loadUser(userDetails.getUsername());
        List<String> roles = extractRoles(userDetails);
        
        String accessToken = jwtService.generateToken(userDetails);
        String rawRefreshToken = generateRawRefreshToken();
        
        // Store hashed refresh token in database
        storeRefreshToken(user, rawRefreshToken);

        return new LoginResponse(
                accessToken,
                rawRefreshToken,
                accessTokenValidSeconds,
                refreshTokenValidSeconds,
                TOKEN_TYPE,
                user.getUserName(),
                user.getFullName(),
                roles
        );
    }

    public LoginResponse refresh(TokenRefreshRequest request) {
        String rawToken = request.getRefreshToken();
        String hash = hashToken(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (storedToken.getRevokedAt() != null) {
            // Revoked token reuse detected! As a security precaution, revoke all active tokens for this user
            List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndRevokedAtIsNull(storedToken.getUser());
            activeTokens.forEach(t -> {
                t.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(t);
            });
            throw new BadCredentialsException("Refresh token was already used or revoked! All sessions revoked.");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        // Revoke the old token
        LocalDateTime now = LocalDateTime.now();
        storedToken.setRevokedAt(now);

        // Generate new access & refresh tokens
        User user = storedToken.getUser();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPasswordHash())
                .disabled(user.getStatus() != UserStatus.ACTIVE)
                .accountLocked(user.getStatus() == UserStatus.LOCKED)
                .authorities(user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .toList())
                .build();

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRawRefreshToken = generateRawRefreshToken();
        String newHash = hashToken(newRawRefreshToken);

        storedToken.setReplacedByTokenHash(newHash);
        refreshTokenRepository.save(storedToken);

        // Save new refresh token
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(newHash)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenValidSeconds))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        List<String> roles = extractRoles(userDetails);

        return new LoginResponse(
                newAccessToken,
                newRawRefreshToken,
                accessTokenValidSeconds,
                refreshTokenValidSeconds,
                TOKEN_TYPE,
                user.getUserName(),
                user.getFullName(),
                roles
        );
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        String hash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    public CurrentUserResponse currentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = loadUser(userDetails.getUsername());
        return new CurrentUserResponse(user.getUserName(), user.getFullName(), extractRoles(userDetails));
    }

    private User loadUser(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userName));
    }

    private List<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
    }

    private String generateRawRefreshToken() {
        byte[] randomBytes = new byte[64];
        SECURE_RANDOM.nextBytes(randomBytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private void storeRefreshToken(User user, String rawRefreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenValidSeconds))
                .build();
        refreshTokenRepository.save(token);
    }

    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}