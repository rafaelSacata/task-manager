package br.com.rafaelaranda.task_manager.task.dto;

import br.com.rafaelaranda.task_manager.reminder.enums.ReminderInterval;
import jakarta.validation.constraints.NotBlank;

public record TaskUpdateDTO(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    boolean hasReminders,

    ReminderInterval reminderInterval
) {}