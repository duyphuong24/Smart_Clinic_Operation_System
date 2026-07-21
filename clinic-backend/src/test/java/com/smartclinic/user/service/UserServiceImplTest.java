package com.smartclinic.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;
    private UserServiceImpl userService;

    private User sampleUser;
    private Role sampleRole;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, roleRepository, userMapper, passwordEncoder);

        sampleRole = new Role(1L, "ROLE_ADMIN");
        sampleUser = User.builder()
                .id(1L)
                .userName("testadmin")
                .passwordHash("hashedpassword")
                .fullName("Test Admin")
                .phone("0900000000")
                .status(UserStatus.ACTIVE)
                .failedLoginAttempts(0)
                .roles(Set.of(sampleRole))
                .build();
    }

    @Test
    void testGetByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getById(1L);

        assertNotNull(response);
        assertEquals("testadmin", response.getUserName());
        assertEquals("Test Admin", response.getFullName());
    }

    @Test
    void testGetByIdNotFoundThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void testCreateUserSuccess() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUserName("newuser");
        request.setPassword("password123");
        request.setFullName("New User");
        request.setPhone("0911111111");
        request.setRoleIds(Set.of(1L));

        when(userRepository.existsByUserName("newuser")).thenReturn(false);
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(sampleRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals("newuser", response.getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicateUsernameThrowsException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUserName("testadmin");
        request.setRoleIds(Set.of(1L));

        when(userRepository.existsByUserName("testadmin")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }

    @Test
    void testUpdateStatusToActiveResetsLockTime() {
        sampleUser.setStatus(UserStatus.LOCKED);
        sampleUser.setFailedLoginAttempts(5);

        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateStatus(1L, request);

        assertEquals(UserStatus.ACTIVE, response.getStatus());
        assertEquals(0, sampleUser.getFailedLoginAttempts());
    }

    @Test
    void testResetPasswordSuccess() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setNewPassword("newSecret123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newSecret123")).thenReturn("encodedNewSecret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.resetPassword(1L, request);

        assertNotNull(response);
        assertEquals("encodedNewSecret", sampleUser.getPasswordHash());
    }
}
