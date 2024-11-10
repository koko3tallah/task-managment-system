package com.kerolos.tms.banquemisr.challenge05.service.impl;

import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskResponse;
import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskPriority;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskStatus;
import com.kerolos.tms.banquemisr.challenge05.data.enums.UserRole;
import com.kerolos.tms.banquemisr.challenge05.service.search.TaskSpecification;
import com.kerolos.tms.banquemisr.challenge05.exception.TaskNotFoundException;
import com.kerolos.tms.banquemisr.challenge05.exception.UnauthorizedException;
import com.kerolos.tms.banquemisr.challenge05.mapper.TaskMapper;
import com.kerolos.tms.banquemisr.challenge05.repository.TaskRepository;
import com.kerolos.tms.banquemisr.challenge05.repository.UserRepository;
import com.kerolos.tms.banquemisr.challenge05.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskMapper.convertToEntity(taskRequest);
        Task savedTask = taskRepository.save(task);
        return taskMapper.convertToResponseDTO(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.convertToResponseDTO(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElse(new User());
        if (!UserRole.ADMIN.equals(user.getRole()) && !existingTask.getCreatedBy().equals(user)) {
            throw new UnauthorizedException("Logged in user is not authorized to update this task.");
        }

        existingTask.setTitle(taskRequest.getTitle());
        existingTask.setDescription(taskRequest.getDescription());
        existingTask.setStatus(taskRequest.getStatus());
        existingTask.setPriority(taskRequest.getPriority());
        existingTask.setDueDate(taskRequest.getDueDate() == null ? null : taskRequest.getDueDate().toLocalDateTime());

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.convertToResponseDTO(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElse(new User());
        if (!UserRole.ADMIN.equals(user.getRole()) && !task.getCreatedBy().equals(user)) {
            throw new UnauthorizedException("Logged in user is not authorized to delete this task.");
        }

        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> getAllUserTasks() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElse(new User());

        List<Task> tasks = taskRepository.findByCreatedBy_Id(user.getId());
        return tasks.stream()
                .map(taskMapper::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> searchTasks(String title, String description, TaskStatus status, TaskPriority priority, OffsetDateTime dueDate) {
        Specification<Task> spec = TaskSpecification.buildSearchCriteria(title, description, status, priority, dueDate == null ? null : dueDate.toLocalDateTime());
        List<Task> tasks = taskRepository.findAll(spec);
        return tasks.stream()
                .map(taskMapper::convertToResponseDTO)
                .collect(Collectors.toList());
    }

}
