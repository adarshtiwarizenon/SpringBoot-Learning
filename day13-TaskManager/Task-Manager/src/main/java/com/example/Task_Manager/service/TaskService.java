package com.example.Task_Manager.service;

import com.example.Task_Manager.dto.TaskRequestDTO;
import com.example.Task_Manager.dto.TaskResponseDTO;
import com.example.Task_Manager.exception.BadRequestException;
import com.example.Task_Manager.exception.ResourceNotFoundException;
import com.example.Task_Manager.model.Category;
import com.example.Task_Manager.model.Task;
import com.example.Task_Manager.model.TaskStatus;
import com.example.Task_Manager.model.User;
import com.example.Task_Manager.repository.TaskRepository;
import com.example.Task_Manager.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       CategoryService categoryService,
                       ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    public List<TaskResponseDTO> getMyTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByOwnerId(currentUser.getId()).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<TaskResponseDTO> getMyTasksByStatus(TaskStatus status) {
        User currentUser = getCurrentUser();
        return taskRepository.findByOwnerIdAndStatus(currentUser.getId(), status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = findTaskEntity(id);
        verifyOwnership(task);
        return toResponseDTO(task);
    }

    public TaskResponseDTO createTask(TaskRequestDTO request) {
        User currentUser = getCurrentUser();
        Category category = categoryService.findCategoryEntity(request.getCategoryId());

        Task task = modelMapper.map(request, Task.class);
        task.setOwner(currentUser);
        task.setCategory(category);

        return toResponseDTO(taskRepository.save(task));
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task existing = findTaskEntity(id);
        verifyOwnership(existing);

        modelMapper.map(request, existing);

        if (request.getCategoryId() != null) {
            Category category = categoryService.findCategoryEntity(request.getCategoryId());
            existing.setCategory(category);
        }

        return toResponseDTO(taskRepository.save(existing));
    }

    public void deleteTask(Long id) {
        Task task = findTaskEntity(id);
        verifyOwnership(task);
        taskRepository.deleteById(id);
    }

    private Task findTaskEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private void verifyOwnership(Task task) {
        User currentUser = getCurrentUser();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't own this task");
        }
    }

    private TaskResponseDTO toResponseDTO(Task task) {
        return modelMapper.map(task, TaskResponseDTO.class);
    }
}