package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.UserResponse;
import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import com.kerolos.tms.banquemisr.challenge05.mapper.UserMapper;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createUserUserAlreadyExistsShouldThrowException() {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("kerolosmalak@gmail.com");
        request.setPassword("password");
        request.setRole(UserRole.USER);
        request.setDateOfBirth(LocalDate.of(1994, 10, 18));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(Exception.class, () -> userService.createUser(request));
        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserNewUserShouldSaveUser() throws Exception {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("kerolosmalak@gmail.com");
        request.setPassword("password");
        request.setRole(UserRole.USER);
        request.setDateOfBirth(LocalDate.of(1994, 10, 18));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        userService.createUser(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsersShouldReturnUserList() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("kerolosmalak@gmail.com");
        userResponse.setFullName("Kerolos Atallah");
        userResponse.setRole(UserRole.USER);

        User user = new User();
        user.setId(1L);
        user.setFullName("Kerolos Atallah");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userResponse);

        List<UserResponse> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("Kerolos Atallah", users.get(0).getFullName());
        verify(userRepository).findAll();
    }

    @Test
    void getUserByIdUserExistsShouldReturnUser() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("kerolosmalak@gmail.com");
        userResponse.setFullName("Kerolos Atallah");
        userResponse.setRole(UserRole.USER);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userResponse);

        Optional<UserResponse> response = userService.getUserById(1L);

        assertTrue(response.isPresent());
        assertEquals("Kerolos Atallah", response.get().getFullName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByIdUserDoesNotExistShouldReturnEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponse> userResponse = userService.getUserById(1L);

        assertFalse(userResponse.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUserUserNotFoundShouldThrowException() {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("kerolosmalak@gmail.com");
        request.setPassword("password");
        request.setRole(UserRole.USER);
        request.setDateOfBirth(LocalDate.of(1994, 10, 18));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserEmailAlreadyExistsShouldThrowException() {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("kerolosmalak@gmail.com");
        request.setPassword("password");
        request.setRole(UserRole.USER);
        request.setDateOfBirth(LocalDate.of(1994, 10, 18));

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("differentuser@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> userService.updateUser(1L, request));
        assertEquals("User with this email already exists", exception.getMessage());
    }

    @Test
    void updateUserValidUpdateShouldSaveUser() throws Exception {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setEmail("kerolosmalak@gmail.com");
        request.setPassword("newpassword");
        request.setRole(UserRole.USER);
        request.setDateOfBirth(LocalDate.of(1994, 10, 18));

        User user = new User();
        user.setId(1L);
        user.setEmail("olduser@example.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("kerolosmalak@gmail.com");
        userResponse.setFullName("Updated User");
        userResponse.setRole(UserRole.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserDTO(any(User.class))).thenReturn(userResponse);
        when(passwordEncoder.encode("newpassword")).thenReturn("hashedNewPassword");

        UserResponse response = userService.updateUser(1L, request);

        assertEquals("Updated User", response.getFullName());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserShouldDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

}