package br.com.rafaelaranda.task_manager.task.dto;

import java.time.LocalDateTime;

import br.com.rafaelaranda.task_manager.reminder.enums.ReminderInterval;

public record TaskResponseDTO(
        String taskId,
        String title,
        String description,
        boolean completed,
        LocalDateTime creationDate,
        LocalDateTime completionDate,
        boolean hasReminders,
        ReminderInterval reminderInterval
) {
}