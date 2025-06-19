package br.com.rafaelaranda.task_manager.task.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskUpdateDTO(
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Description is required")
        String description,
        boolean completed
) {
}
