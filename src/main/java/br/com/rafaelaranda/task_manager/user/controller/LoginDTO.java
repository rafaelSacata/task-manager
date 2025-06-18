package br.com.rafaelaranda.task_manager.user.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {}
