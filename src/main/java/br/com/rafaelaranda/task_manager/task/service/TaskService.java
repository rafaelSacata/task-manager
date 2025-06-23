package br.com.rafaelaranda.task_manager.task.service;

import br.com.rafaelaranda.task_manager.task.dto.TaskConcludeDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskCreateDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskResponseDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskUpdateDTO;
import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.task.repository.TaskRepository;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Creating task for user: {}", user.getEmail().toString());

        if (dto.hasReminders() && dto.reminderInterval() == null) {
            throw new ValidationException("Reminder interval is required when reminders are enabled");
        }
    
        if (!dto.hasReminders() && dto.reminderInterval() != null) {
            throw new ValidationException("Reminder interval must be null when reminders are disabled");
        }

        TaskEntity task = new TaskEntity();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setUser(user);
        task.setCompleted(false);
        task.setHasReminders(dto.hasReminders());
        task.setReminderInterval(dto.reminderInterval());
        saveTask(task);;
        return toResponseDTO(task);
    }

    public TaskResponseDTO updateTask(UUID id, TaskUpdateDTO dto) {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Updating task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to update task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        if (!task.isCompleted()) {
            task.setHasReminders(dto.hasReminders()); 
            task.setReminderInterval(dto.reminderInterval());
            task.setDescription(dto.description());
            task.setTitle(dto.title());
            saveTask(task);;
        }
        return toResponseDTO(task);
    }

    public TaskResponseDTO concludeTask(UUID id, TaskConcludeDTO dto) {
        UserEntity user = userService.getAuthenticatedUser();
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        LOGGER.info("Updating task {} for user: {}", id, user.getEmail().toString());
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to update task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        if (dto.completed() && !task.isCompleted()) {
            task.setCompleted(dto.completed());
            task.setCompletionDate(LocalDateTime.now());
            task.setHasReminders(false); 
            task.setReminderInterval(null);
            saveTask(task);;
        }
        return toResponseDTO(task);
    }

    public void deleteTask(UUID id) {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Deleting task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to delete task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        deleteTask(task);
    }

    public TaskResponseDTO getTask(UUID id) {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Retrieving task {} for user: {}", id, user.getEmail().toString());
        TaskEntity task = findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + id));
        if (!task.getUser().getUserId().equals(user.getUserId())) {
            LOGGER.warn("User {} attempted to access task {} owned by another user", user.getEmail(), id);
            throw new SecurityException("Access denied: Task does not belong to user");
        }
        return toResponseDTO(task);
    }

    public List<TaskResponseDTO> getAllTasks() {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Retrieving all tasks for user: {}", user.getEmail().toString());
        List<TaskEntity> tasks = findTasksByUser(user);
        return tasks.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getPendingReminders() {
        UserEntity user = userService.getAuthenticatedUser();
        LOGGER.info("Retrieving pending reminders for user: {}", user.getEmail().toString());
        LocalDateTime now = LocalDateTime.now();
        List<TaskEntity> tasks = taskRepository.findPendingReminders(user, now);
        for (TaskEntity task : tasks) {
            task.setLastReminderSent(now);
            taskRepository.save(task);
        }
        return tasks.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<TaskEntity> findTasksWithActiveReminders() {
        return taskRepository.findByHasRemindersTrueAndCompletedFalse();
    }

    public Optional<TaskEntity> findTaskById(UUID id) {
        return taskRepository.findById(id);
    }

    public List<TaskEntity> findTasksByUser(UserEntity userEntity) {
        return taskRepository.findByUser(userEntity);
    }

    public void saveTask(TaskEntity taskEntity) {
        taskRepository.save(taskEntity);
    }

    public void deleteTask(TaskEntity taskEntity) {
        taskRepository.delete(taskEntity);
    }

    private TaskResponseDTO toResponseDTO(TaskEntity task) {
        return new TaskResponseDTO(
                task.getTaskId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreationDate(),
                task.getCompletionDate(),
                task.isHasReminders(),
                task.getReminderInterval()
        );
    }
}