package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.RefreshTokenRequest;
import com.kerolos.tms.banquemisr.challenge05.data.entity.RefreshToken;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.repository.RefreshTokenRepository;
import com.kerolos.tms.banquemisr.challenge05.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(10000));

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateRefreshToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken createdToken = refreshTokenService.createRefreshToken(user);
        assertNotNull(createdToken);
        assertEquals(user, createdToken.getUser());
    }

    @Test
    public void testRefreshAccessTokenValidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken.getToken());

        when(refreshTokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.generateToken(user)).thenReturn("newAccessToken");

        JwtResponse response = refreshTokenService.refreshAccessToken(request);
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
    }

    @Test
    public void testRefreshAccessTokenInvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalidToken");

        when(refreshTokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> refreshTokenService.refreshAccessToken(request));
    }

    @Test
    public void testIsValidValidToken() {
        assertTrue(refreshTokenService.isValid(refreshToken));
    }

    @Test
    public void testIsValidExpiredToken() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(10000));
        assertFalse(refreshTokenService.isValid(refreshToken));
    }

    @Test
    public void testDeleteByToken() {
        doNothing().when(refreshTokenRepository).deleteByToken(refreshToken.getToken());
        refreshTokenService.deleteByToken(refreshToken.getToken());
        verify(refreshTokenRepository, times(1)).deleteByToken(refreshToken.getToken());
    }

    @Test
    public void testFindByToken() {
        when(refreshTokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(refreshToken.getToken());
        assertTrue(foundToken.isPresent());
        assertEquals(refreshToken, foundToken.get());
    }
}
