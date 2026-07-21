package com.smartclinic.user.service;

import com.smartclinic.common.dto.PageResponse;
import com.smartclinic.user.dto.ResetPasswordRequest;
import com.smartclinic.user.dto.UserCreateRequest;
import com.smartclinic.user.dto.UserResponse;
import com.smartclinic.user.dto.UserStatusUpdateRequest;
import com.smartclinic.user.dto.UserUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponse> search(String keyword, Pageable pageable);

    UserResponse getById(Long id);

    UserResponse create(UserCreateRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    UserResponse updateStatus(Long id, UserStatusUpdateRequest request);

    UserResponse resetPassword(Long id, ResetPasswordRequest request);
}
