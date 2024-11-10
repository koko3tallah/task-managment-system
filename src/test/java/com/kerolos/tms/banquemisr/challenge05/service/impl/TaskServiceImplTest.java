package com.kerolos.tms.banquemisr.challenge05.service.impl;


import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskResponse;
import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import com.kerolos.tms.banquemisr.challenge05.exception.TaskNotFoundException;
import com.kerolos.tms.banquemisr.challenge05.exception.UnauthorizedException;
import com.kerolos.tms.banquemisr.challenge05.mapper.TaskMapper;
import com.kerolos.tms.banquemisr.challenge05.repository.TaskRepository;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskRequest taskRequest;
    private TaskResponse taskResponse;
    private User user;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Task 1");

        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("Task 1");

        user = new User();
        user.setId(1L);
        user.setEmail("kerolosmalak@gmail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateTaskSuccess() {
        when(taskMapper.convertToEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.convertToResponseDTO(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.createTask(taskRequest);

        assertNotNull(response);
        assertEquals(taskResponse.getId(), response.getId());
    }

    @Test
    void testGetTaskByIdSuccess() {
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        when(taskMapper.convertToResponseDTO(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(taskResponse.getId(), response.getId());
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void testUpdateTaskUnauthorizedUser() {
        task.setCreatedBy(new User());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> taskService.updateTask(1L, taskRequest));
    }

    @Test
    void testUpdateTaskAdminUser() {
        user.setRole(UserRole.ADMIN);
        task.setCreatedBy(new User());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> taskService.updateTask(1L, taskRequest));
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTaskTaskNotFound() {
        when(taskRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }

    @Test
    void testSearchTasksWithCriteria() {
        when(taskRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(task));
        when(taskMapper.convertToResponseDTO(any(Task.class))).thenReturn(taskResponse);

        List<TaskResponse> responseList = taskService.searchTasks("Task", null, null, null, OffsetDateTime.now());

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals(1L, responseList.get(0).getId());
    }
}