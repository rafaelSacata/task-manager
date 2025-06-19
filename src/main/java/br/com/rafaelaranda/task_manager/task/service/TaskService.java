package br.com.rafaelaranda.task_manager.task.service;

import br.com.rafaelaranda.task_manager.task.dto.TaskCreateDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskResponseDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskUpdateDTO;
import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.task.repository.TaskRepository;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        UserEntity user = getAuthenticatedUser();
        LOGGER.info("Creating task for user: {}", user.getEmail().toString());
        TaskEntity task = new TaskEntity();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setUser(user);
        task.setCompleted(false);
        task = taskRepository.save(task);
        return toResponseDTO(task);
    }

    public TaskResponseDTO updateTask(UUID id, TaskUpdateDTO dto) {
        UserEntity user = getAuthenticatedUser();
        LOGGER.info("Updating task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to update task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        if (dto.completed() && !task.isCompleted()) {
            task.setCompleted(true);
            task.setCompletionDate(LocalDateTime.now());
        } else if (!dto.completed() && task.isCompleted()) {
            task.setCompleted(false);
            task.setCompletionDate(null);
        }
        task = taskRepository.save(task);
        return toResponseDTO(task);
    }

    public void deleteTask(UUID id) {
        UserEntity user = getAuthenticatedUser();
        LOGGER.info("Deleting task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to delete task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        taskRepository.delete(task);
    }

    public TaskResponseDTO getTask(UUID id) {
        UserEntity user = getAuthenticatedUser();
        LOGGER.info("Retrieving task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to access task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        return toResponseDTO(task);
    }

    public List<TaskResponseDTO> getAllTasks() {
        UserEntity user = getAuthenticatedUser();
        LOGGER.info("Retrieving all tasks for user: {}", user.getEmail().toString());
        List<TaskEntity> tasks = taskRepository.findByUser(user);
        return tasks.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private UserEntity getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            LOGGER.error("No authenticated user found");
            throw new SecurityException("No authenticated user found");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        UserEntity user = userRepository.findByEmail(Email.of(email));
        if (user == null) {
            LOGGER.error("User not found: {}", email);
            throw new EntityNotFoundException("User not found: " + email);
        }
        return user;
    }

    private TaskResponseDTO toResponseDTO(TaskEntity task) {
        return new TaskResponseDTO(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreationDate(),
                task.getCompletionDate()
        );
    }
}