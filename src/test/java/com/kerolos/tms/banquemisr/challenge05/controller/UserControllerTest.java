package com.kerolos.tms.banquemisr.challenge05.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kerolos.tms.banquemisr.challenge05.data.dto.AdminSignupRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.UserResponse;
import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import com.kerolos.tms.banquemisr.challenge05.service.UserService;
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

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest()
@ContextConfiguration(classes = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    ObjectMapper objectMapper = JsonUtils.getJacksonObjectMapper();

    @Test
    void createUserShouldReturnOkWhenUserIsCreated() throws Exception {
        AdminSignupRequest request = new AdminSignupRequest();
        request.setRole(UserRole.USER);
        request.setFullName("Kerolos");
        request.setPassword("password");
        request.setEmail("kerolosmalak@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/" + UserController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User created successfully"));

        Mockito.verify(userService, times(1)).createUser(any(AdminSignupRequest.class));
    }

    @Test
    void getAllUsersShouldReturnOkWhenUsersExist() throws Exception {
        UserResponse userResponse = new UserResponse();

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/" + UserController.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByIdShouldReturnOkWhenUserExists() throws Exception {
        Long userId = 1L;
        UserResponse userResponse = new UserResponse();

        when(userService.getUserById(userId)).thenReturn(Optional.of(userResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/" + UserController.PATH + "/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void updateUserShouldReturnOkWhenUserIsUpdated() throws Exception {
        Long userId = 1L;
        AdminSignupRequest request = new AdminSignupRequest();
        request.setRole(UserRole.USER);

        mockMvc.perform(MockMvcRequestBuilders.put("/" + UserController.PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User Updated successfully"));

        Mockito.verify(userService, times(1)).updateUser(eq(userId), any(AdminSignupRequest.class));
    }

    @Test
    void deleteUserShouldReturnOkWhenUserIsDeleted() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/" + UserController.PATH + "/" + userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted successfully"));

        Mockito.verify(userService, times(1)).deleteUser(userId);
    }
}