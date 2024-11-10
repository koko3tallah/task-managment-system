package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.ChangePasswordRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.LoginRequest;
import com.kerolos.tms.banquemisr.challenge05.data.entity.RefreshToken;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import com.kerolos.tms.banquemisr.challenge05.security.JwtUtils;
import com.kerolos.tms.banquemisr.challenge05.security.TokenBlacklistService;
import com.kerolos.tms.banquemisr.challenge05.service.RefreshTokenService;
import com.kerolos.tms.banquemisr.challenge05.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    private User user;
    private LoginRequest loginRequest;
    private AdminSignupRequest signupRequest;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("kerolosmalak@gmail.com");
        user.setPassword("encodedPassword");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("kerolosmalak@gmail.com");
        loginRequest.setPassword("password");

        signupRequest = new AdminSignupRequest();
        changePasswordRequest = new ChangePasswordRequest("currentPassword", "newPassword");
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(user)).thenReturn("jwtToken");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(new RefreshToken());

        JwtResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
    }

    @Test
    public void testLoginUserNotFound() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    public void testLoginInvalidCredentials() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(Exception.class, () -> authenticationService.login(loginRequest));
    }

    @Test
    public void testSignup() throws Exception {
        authenticationService.signup(signupRequest);
        verify(userService).createUser(signupRequest);
    }

    @Test
    public void testLogout() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authenticationService.logout(mock(HttpServletRequest.class), authentication);

        verify(tokenBlacklistService).addToBlacklist(any());
        verify(refreshTokenService).deleteByUser(user);
    }

    @Test
    public void testLogoutUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.logout(mock(HttpServletRequest.class), authentication));
    }

    @Test
    public void testChangePasswordSuccess() throws Exception {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("newEncodedPassword");

        authenticationService.changePassword(changePasswordRequest, user.getEmail());

        verify(userRepository).save(user);
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    public void testChangePasswordUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authenticationService.changePassword(changePasswordRequest, user.getEmail()));
    }

    @Test
    public void testChangePasswordInvalidCurrentPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(Exception.class, () -> authenticationService.changePassword(changePasswordRequest, user.getEmail()));
    }
}