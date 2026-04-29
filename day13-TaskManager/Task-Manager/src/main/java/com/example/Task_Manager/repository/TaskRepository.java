package com.example.Task_Manager.repository;

import com.example.Task_Manager.model.Task;
import com.example.Task_Manager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerId(Long ownerId);
    List<Task> findByOwnerIdAndStatus(Long ownerId, TaskStatus status);
    List<Task> findByCategoryId(Long categoryId);
}