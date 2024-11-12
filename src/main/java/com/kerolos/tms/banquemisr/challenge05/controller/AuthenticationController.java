package com.kerolos.tms.banquemisr.challenge05.controller;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.ChangePasswordRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.JwtResponse;
import com.kerolos.tms.banquemisr.challenge05.data.dto.LoginRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.RefreshTokenRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.SignupRequest;
import com.kerolos.tms.banquemisr.challenge05.service.AuthenticationService;
import com.kerolos.tms.banquemisr.challenge05.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AuthenticationController.PATH)
public class AuthenticationController {

    public static final String PATH = "v1/auth";

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "Login user", description = "Authenticate a user using email and password to obtain a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or authentication failed", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authenticationService.login(loginRequest));
        } catch (Exception e) {
            log.error("Login attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token or request", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            JwtResponse jwtResponse = refreshTokenService.refreshAccessToken(refreshTokenRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            log.error("Refresh token attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "User signup", description = "Register a new user by providing their full name, email, password, and date of birth, with a default role of (USER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input or registration failed", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            authenticationService.signup(new AdminSignupRequest(signupRequest));
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("Signup attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(summary = "User logout", description = "Logs out the authenticated user and invalidates the session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Logout attempt failed", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, Authentication authentication) {
        try {
            authenticationService.logout(request, authentication);
            return ResponseEntity.ok("User logged out successfully.");
        } catch (Exception e) {
            log.error("Logout attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Change user password",
            description = "Allows an authenticated user to change their password by providing the current password and a new password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid request or password change failed", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        try {
            authenticationService.changePassword(request, userDetails.getUsername());
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
