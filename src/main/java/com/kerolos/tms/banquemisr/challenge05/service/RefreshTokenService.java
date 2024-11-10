package com.kerolos.tms.banquemisr.challenge05.service;

import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.RefreshTokenRequest;
import com.kerolos.tms.banquemisr.challenge05.data.entity.RefreshToken;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;

import java.util.Optional;

/**
 * RefreshTokenService provides the contract for managing refresh tokens, including their creation,
 * validation, and deletion. It also facilitates refreshing access tokens using valid refresh tokens.
 */
public interface RefreshTokenService {

    /**
     * Creates a new refresh token for the specified user.
     *
     * @param user the {@link User} for whom the refresh token is created.
     * @return the newly created {@link RefreshToken}.
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Refreshes the access token using a valid refresh token provided in the request.
     *
     * @param refreshTokenRequest the {@link RefreshTokenRequest} containing the refresh token.
     * @return a {@link JwtResponse} containing a new access token and possibly a new refresh token.
     */
    JwtResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * Validates whether the provided refresh token is still valid and not expired.
     *
     * @param refreshToken the {@link RefreshToken} to validate.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    boolean isValid(RefreshToken refreshToken);

    /**
     * Finds a refresh token by its token string.
     *
     * @param token the refresh token string to search for.
     * @return an {@link Optional} containing the {@link RefreshToken} if found, or an empty {@link Optional} if not found.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes a refresh token by its token string.
     *
     * @param token the refresh token string to delete.
     */
    void deleteByToken(String token);

    /**
     * Deletes all refresh tokens associated with the specified user.
     *
     * @param user the {@link User} for whom all refresh tokens should be deleted.
     */
    void deleteByUser(User user);

}
