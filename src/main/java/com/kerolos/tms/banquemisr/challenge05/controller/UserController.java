package com.kerolos.tms.banquemisr.challenge05.controller;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.UserResponse;
import com.kerolos.tms.banquemisr.challenge05.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(UserController.PATH)
@RequiredArgsConstructor
public class UserController {

    public static final String PATH = "v1/users";

    private final UserService userService;


    @Operation(summary = "Create a new user", description = "Allows an Admin to create a new user with a specific role and credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input or user creation error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden, user does not have sufficient permissions", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody AdminSignupRequest adminSignupRequest) {
        try {
            userService.createUser(adminSignupRequest);
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            log.error("Create user attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users. Only accessible by Admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden, user does not have sufficient permissions", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @Operation(summary = "Get user by ID", description = "Retrieve a specific user's details by their ID. Only accessible by Admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden, user does not have sufficient permissions", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@Parameter(description = "ID of the user to retrieve", required = true)
                                                    @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Update user", description = "Allows an Admin to update the details of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input or update error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden, user does not have sufficient permissions", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
                                        @Parameter(description = "Updated user details", required = true) @RequestBody AdminSignupRequest updatedUser) {
        try {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User Updated successfully");
        } catch (Exception e) {
            log.error("Update user attempt failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(summary = "Delete user", description = "Allows an Admin to delete a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized, user is not authenticated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden, user does not have sufficient permissions", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@Parameter(description = "ID of the user to delete", required = true)
                                        @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
