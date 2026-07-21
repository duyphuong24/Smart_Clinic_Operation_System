package com.smartclinic.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotEmpty(message = "At least one role ID must be provided")
    private Set<Long> roleIds;
}
