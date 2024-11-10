package com.kerolos.tms.banquemisr.challenge05.service;

import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskResponse;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskPriority;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskStatus;
import com.kerolos.tms.banquemisr.challenge05.exception.TaskNotFoundException;
import com.kerolos.tms.banquemisr.challenge05.exception.UnauthorizedException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface TaskService {

    /**
     * Creates a new task based on the provided request data.
     *
     * @param taskRequest the task data for creation
     * @return the created task as a response DTO
     */
    TaskResponse createTask(TaskRequest taskRequest);

    /**
     * Retrieves a task by its unique ID.
     *
     * @param id the ID of the task to retrieve
     * @return the task as a response DTO
     * @throws TaskNotFoundException if no task is found with the given ID
     */
    TaskResponse getTaskById(Long id);

    /**
     * Retrieves all tasks in the system.
     *
     * @return a list of all tasks as response DTOs
     */
    List<TaskResponse> getAllTasks();

    /**
     * Updates an existing task with the provided request data.
     * <p>
     * Only the user who created the task or an admin user is authorized to update it.
     * </p>
     *
     * @param taskId      the ID of the task to update
     * @param taskRequest the updated task data
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if no task is found with the given ID
     * @throws UnauthorizedException if the user is not authorized to update the task
     */
    TaskResponse updateTask(Long taskId, TaskRequest taskRequest);

    /**
     * Deletes a task by its unique ID.
     * <p>
     * Only the user who created the task or an admin user is authorized to delete it.
     * </p>
     *
     * @param taskId the ID of the task to delete
     * @throws TaskNotFoundException if no task is found with the given ID
     * @throws UnauthorizedException if the user is not authorized to delete the task
     */
    void deleteTask(Long taskId);

    /**
     * Retrieves all tasks created by the currently authenticated user.
     *
     * @return a list of tasks created by the authenticated user as response DTOs
     */
    List<TaskResponse> getAllUserTasks();

    /**
     * Searches for tasks based on specified filter criteria.
     *
     * @param title       optional filter for task title
     * @param description optional filter for task description
     * @param status      optional filter for task status
     * @param priority    optional filter for task priority
     * @param dueDate     optional filter for task due date
     * @return a list of tasks that match the given criteria
     */
    List<TaskResponse> searchTasks(String title, String description, TaskStatus status, TaskPriority priority, OffsetDateTime dueDate);

}
