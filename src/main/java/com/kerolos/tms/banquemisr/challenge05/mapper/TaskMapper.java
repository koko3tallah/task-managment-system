package com.kerolos.tms.banquemisr.challenge05.mapper;

import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskRequest;
import com.kerolos.tms.banquemisr.challenge05.data.dto.TaskResponse;
import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse convertToResponseDTO(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getModifiedAt());
        return response;
    }

    public Task convertToEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate() == null ? null : request.getDueDate().toLocalDateTime());
        return task;
    }

}
