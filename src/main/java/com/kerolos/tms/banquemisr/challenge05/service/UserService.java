package com.kerolos.tms.banquemisr.challenge05.service;

import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.UserResponse;

import java.util.List;
import java.util.Optional;

/**
 * UserService provides the contract for managing user-related operations such as
 * creating, updating, deleting, and retrieving user details. It includes functionality
 * for user management by administrators.
 */
public interface UserService {

    /**
     * Creates a new user with the specified details.
     *
     * @param adminSignupRequest the {@link AdminSignupRequest} containing user information such as full name,
     *                           email, password, role, and date of birth.
     * @throws Exception if the user creation fails, such as when the email already exists or validation errors occur.
     */
    void createUser(AdminSignupRequest adminSignupRequest) throws Exception;

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of {@link UserResponse} objects representing all registered users.
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return an {@link Optional} containing the {@link UserResponse} if the user is found, or an empty {@link Optional} if not found.
     */
    Optional<UserResponse> getUserById(Long id);

    /**
     * Updates the details of an existing user by their ID.
     *
     * @param id          the ID of the user to update.
     * @param updatedUser the {@link AdminSignupRequest} containing the updated user details such as full name,
     *                    email, password, and role.
     * @return the updated {@link UserResponse} representing the user's updated information.
     * @throws Exception if the user update fails, such as when the user is not found or validation errors occur.
     */
    UserResponse updateUser(Long id, AdminSignupRequest updatedUser) throws Exception;

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    void deleteUser(Long id);

}
