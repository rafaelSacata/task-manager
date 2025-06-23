package br.com.rafaelaranda.task_manager.task.dto;

import jakarta.validation.constraints.AssertTrue;

public record TaskConcludeDTO(

        @AssertTrue(message = "Task must be completed") boolean completed) {
}
