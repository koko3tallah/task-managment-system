package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.RefreshTokenRequest;
import com.kerolos.tms.banquemisr.challenge05.data.entity.RefreshToken;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.repository.RefreshTokenRepository;
import com.kerolos.tms.banquemisr.challenge05.security.JwtUtils;
import com.kerolos.tms.banquemisr.challenge05.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refreshTokenDurationMs}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public JwtResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        String requestToken = refreshTokenRequest.getRefreshToken();
        RefreshToken refreshToken = this.findByToken(requestToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!this.isValid(refreshToken)) {
            this.deleteByToken(refreshToken.getToken());
            throw new RuntimeException("Refresh token is expired. Please login again.");
        }

        String newAccessToken = jwtUtils.generateToken(refreshToken.getUser());
        return new JwtResponse(newAccessToken, requestToken);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    public boolean isValid(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
