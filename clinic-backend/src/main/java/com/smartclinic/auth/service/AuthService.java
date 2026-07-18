package com.smartclinic.auth.service;

import com.smartclinic.auth.dto.CurrentUserResponse;
import com.smartclinic.auth.dto.LoginRequest;
import com.smartclinic.auth.dto.LoginResponse;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.security.JwtService;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = loadUser(userDetails.getUsername());
        List<String> roles = extractRoles(userDetails);
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, TOKEN_TYPE, user.getUserName(), user.getFullName(), roles);
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
}