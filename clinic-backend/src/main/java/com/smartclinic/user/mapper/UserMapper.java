package com.smartclinic.user.mapper;

import com.smartclinic.user.dto.UserResponse;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockTime(user.getLockTime())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                        : Set.of())
                .build();
    }
}
