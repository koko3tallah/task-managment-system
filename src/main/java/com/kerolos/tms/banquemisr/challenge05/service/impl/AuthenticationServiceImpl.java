package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.ChangePasswordRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.LoginRequest;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import com.kerolos.tms.banquemisr.challenge05.security.JwtUtils;
import com.kerolos.tms.banquemisr.challenge05.security.TokenBlacklistService;
import com.kerolos.tms.banquemisr.challenge05.service.AuthenticationService;
import com.kerolos.tms.banquemisr.challenge05.service.RefreshTokenService;
import com.kerolos.tms.banquemisr.challenge05.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    public JwtResponse login(LoginRequest loginRequest) throws Exception {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Exception("Invalid credentials");
        }

        refreshTokenService.deleteByUser(user);

        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return new JwtResponse(jwtToken, refreshToken);
    }


    @Override
    public void signup(AdminSignupRequest signupRequest) throws Exception {
        userService.createUser(signupRequest);
    }

    @Override
    public void logout(HttpServletRequest request, Authentication authentication) throws Exception {
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        tokenBlacklistService.addToBlacklist(jwtUtils.extractToken(request));
        refreshTokenService.deleteByUser(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new Exception("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
