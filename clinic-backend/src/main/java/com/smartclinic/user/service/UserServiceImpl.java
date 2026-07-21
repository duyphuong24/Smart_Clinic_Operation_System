package com.smartclinic.user.service;

import com.smartclinic.common.dto.PageResponse;
import com.smartclinic.common.exception.BadRequestException;
import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.user.dto.ResetPasswordRequest;
import com.smartclinic.user.dto.UserCreateRequest;
import com.smartclinic.user.dto.UserResponse;
import com.smartclinic.user.dto.UserStatusUpdateRequest;
import com.smartclinic.user.dto.UserUpdateRequest;
import com.smartclinic.user.entity.Role;
import com.smartclinic.user.entity.User;
import com.smartclinic.user.entity.UserStatus;
import com.smartclinic.user.mapper.UserMapper;
import com.smartclinic.user.repository.RoleRepository;
import com.smartclinic.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> search(String keyword, Pageable pageable) {
        String searchTerm = keyword != null ? keyword.trim() : "";
        Page<User> page = userRepository.search(searchTerm, pageable);
        List<UserResponse> items = page.getContent().stream()
                .map(userMapper::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new DuplicateResourceException("Username '" + request.getUserName() + "' is already taken");
        }

        Set<Role> roles = fetchRolesByIds(request.getRoleIds());

        User user = User.builder()
                .userName(request.getUserName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .failedLoginAttempts(0)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findUserById(id);
        Set<Role> roles = fetchRolesByIds(request.getRoleIds());

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(Long id, UserStatusUpdateRequest request) {
        User user = findUserById(id);
        user.setStatus(request.getStatus());

        if (request.getStatus() == UserStatus.ACTIVE) {
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse resetPassword(Long id, ResetPasswordRequest request) {
        User user = findUserById(id);
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Set<Role> fetchRolesByIds(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BadRequestException("Role IDs must not be empty");
        }
        List<Role> roleList = roleRepository.findAllById(roleIds);
        if (roleList.size() != roleIds.size()) {
            throw new ResourceNotFoundException("One or more roles not found with provided IDs");
        }
        return new HashSet<>(roleList);
    }
}
