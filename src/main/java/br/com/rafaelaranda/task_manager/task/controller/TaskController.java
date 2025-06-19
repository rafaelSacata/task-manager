package br.com.rafaelaranda.task_manager.task.controller;

import br.com.rafaelaranda.task_manager.task.dto.TaskCreateDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskResponseDTO;
import br.com.rafaelaranda.task_manager.task.dto.TaskUpdateDTO;
import br.com.rafaelaranda.task_manager.task.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskCreateDTO dto) {
        LOGGER.info("Received request to create task: {}", dto.title());
        TaskResponseDTO response = taskService.createTask(dto);
        return ResponseEntity.created(URI.create("/tasks/" + response.taskId())).body(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable UUID taskId, @Valid @RequestBody TaskUpdateDTO dto) {
        LOGGER.info("Received request to update task: {}", taskId);
        TaskResponseDTO response = taskService.updateTask(taskId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        LOGGER.info("Received request to delete task: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable UUID taskId) {
        LOGGER.info("Received request to get task: {}", taskId);
        TaskResponseDTO response = taskService.getTask(taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        LOGGER.info("Received request to get all tasks");
        List<TaskResponseDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
}
