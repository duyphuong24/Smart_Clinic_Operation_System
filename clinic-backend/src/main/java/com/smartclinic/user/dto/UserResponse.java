package com.smartclinic.user.dto;

import com.smartclinic.user.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String userName;
    private String fullName;
    private String phone;
    private UserStatus status;
    private Integer failedLoginAttempts;
    private LocalDateTime lockTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> roles;
}
