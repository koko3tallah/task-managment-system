package com.kerolos.tms.banquemisr.challenge05.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.LoginRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.RefreshTokenRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.SignupRequest;
import com.kerolos.tms.banquemisr.challenge05.service.AuthenticationService;
import com.kerolos.tms.banquemisr.challenge05.service.RefreshTokenService;
import com.kerolos.tms.banquemisr.challenge05.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest()
@ContextConfiguration(classes = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private RefreshTokenService refreshTokenService;


    ObjectMapper objectMapper = JsonUtils.getJacksonObjectMapper();

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("kerolos@email.com", "password");
        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");

        when(authenticationService.login(Mockito.any())).thenReturn(jwtResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("kerolos@email.com", "wrongpassword");

        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid credentials"));
    }

    @Test
    public void testRefreshAccessTokenSuccess() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(UUID.randomUUID().toString());
        JwtResponse jwtResponse = new JwtResponse("newToken", null);

        when(refreshTokenService.refreshAccessToken(any(RefreshTokenRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newToken"));
    }

    @Test
    public void testRefreshAccessTokenFailure() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(UUID.randomUUID().toString());

        when(refreshTokenService.refreshAccessToken(any(RefreshTokenRequest.class))).thenThrow(new RuntimeException("Invalid refresh token"));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid refresh token"));
    }

    @Test
    public void testSignupSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest("Kerolos Atallah", "kerolos@email.com", "password", LocalDate.of(1990, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
    }

    @Test
    public void testSignupFailure() throws Exception {
        SignupRequest signupRequest = new SignupRequest("Kerolos Atallah", "kerolos@email.com", "password", LocalDate.of(1990, 1, 1));

        doThrow(new RuntimeException("Email already exists")).when(authenticationService).signup(any());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Email already exists"));
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/logout"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User logged out successfully."));
    }

}