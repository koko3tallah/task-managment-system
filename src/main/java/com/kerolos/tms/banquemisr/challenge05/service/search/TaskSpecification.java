package com.kerolos.tms.banquemisr.challenge05.service.search;

import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskPriority;
import com.kerolos.tms.banquemisr.challenge05.data.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> buildSearchCriteria(String title, String description, TaskStatus status, TaskPriority priority, LocalDateTime dueDate) {
        return Specification.where(titleContains(title))
                .and(descriptionContains(description))
                .and(statusIs(status))
                .and(priorityIs(priority))
                .and(dueDateIs(dueDate));
    }

    private static Specification<Task> titleContains(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    private static Specification<Task> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> description == null ? null : criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    private static Specification<Task> statusIs(TaskStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Task> priorityIs(TaskPriority priority) {
        return (root, query, criteriaBuilder) -> priority == null ? null : criteriaBuilder.equal(root.get("priority"), priority);
    }


    private static Specification<Task> dueDateIs(LocalDateTime dueDate) {
        return (root, query, criteriaBuilder) -> dueDate == null ? null : criteriaBuilder.equal(root.get("dueDate"), dueDate);
    }
}
