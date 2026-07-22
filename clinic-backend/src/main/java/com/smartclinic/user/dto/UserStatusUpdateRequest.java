package com.smartclinic.user.dto;

import com.smartclinic.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;
}
