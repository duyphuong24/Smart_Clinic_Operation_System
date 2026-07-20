package com.smartclinic.security;

import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        if (user.getStatus() == UserStatus.LOCKED && user.getLockTime() != null) {
            if (user.getLockTime().plusMinutes(30).isBefore(java.time.LocalDateTime.now())) {
                user.setStatus(UserStatus.ACTIVE);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        boolean enabled = user.getStatus() == UserStatus.ACTIVE;
        boolean accountNonLocked = user.getStatus() != UserStatus.LOCKED;

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPasswordHash(),
                enabled,
                true,
                true,
                accountNonLocked,
                authorities
        );
    }
}