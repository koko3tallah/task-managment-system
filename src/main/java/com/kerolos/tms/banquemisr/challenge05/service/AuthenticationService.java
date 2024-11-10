package com.kerolos.tms.banquemisr.challenge05.service;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.ChangePasswordRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.LoginRequest;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthenticationService provides the contract for user authentication, registration,
 * and logout functionalities. This service handles login, signup, and logout requests.
 */
public interface AuthenticationService {

    /**
     * Authenticates a user based on login credentials and generates a JWT token upon success.
     *
     * @param loginRequest the {@link LoginRequest} containing the user's email and password.
     * @return a {@link JwtResponse} containing the JWT access token and refresh token.
     * @throws Exception if authentication fails or any other error occurs during login.
     */
    JwtResponse login(LoginRequest loginRequest) throws Exception;

    /**
     * Registers a new user with the specified details.
     *
     * @param signupRequest the {@link AdminSignupRequest} containing the user details such as full name,
     *                      email, password, date of birth, and role.
     * @throws Exception if the registration fails, such as if the email already exists or validation errors occur.
     */
    void signup(AdminSignupRequest signupRequest) throws Exception;

    /**
     * Logs out the authenticated user and invalidates the current session or token.
     *
     * @param request        the {@link HttpServletRequest} containing the user's request details.
     * @param authentication the {@link Authentication} object representing the authenticated user.
     * @throws Exception if an error occurs during logout or if the user is not authenticated.
     */
    void logout(HttpServletRequest request, Authentication authentication) throws Exception;

    /**
     * Changes the password for the user associated with the provided email.
     *
     * @param request   the {@link ChangePasswordRequest} containing the current password and the new password
     * @param userEmail the email of the user whose password is to be changed
     * @throws Exception if the current password does not match or any other issue occurs during the password change process
     */
    void changePassword(ChangePasswordRequest request, String userEmail) throws Exception;

}
