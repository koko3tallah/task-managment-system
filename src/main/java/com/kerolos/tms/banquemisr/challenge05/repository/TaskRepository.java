package com.kerolos.tms.banquemisr.challenge05.repository;

import com.kerolos.tms.banquemisr.challenge05.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByCreatedBy_Id(Long userId);
}
