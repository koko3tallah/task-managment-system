package com.kerolos.tms.banquemisr.challenge05.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
    }
}
