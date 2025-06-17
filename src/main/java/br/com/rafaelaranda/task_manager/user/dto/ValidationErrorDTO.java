package br.com.rafaelaranda.task_manager.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    List<String> messages,
    String path) {}
