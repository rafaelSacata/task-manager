package br.com.rafaelaranda.task_manager.task.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDTO(
        UUID taskId,
        String title,
        String description,
        boolean completed,
        LocalDateTime creationDate,
        LocalDateTime completionDate
) {
}
