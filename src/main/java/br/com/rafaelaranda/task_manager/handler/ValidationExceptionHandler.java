package br.com.rafaelaranda.task_manager.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.rafaelaranda.task_manager.user.dto.ValidationErrorDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class ValidationExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                errors,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ValidationErrorDTO> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String errorMessage = "The provided data conflicted with existing records.";

        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            errorMessage = "There is already a record with the data provided (e.g. email already registered).";
        }

        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Data Conflict",
                List.of(errorMessage),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorDTO> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ValidationErrorDTO> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ValidationErrorDTO> handleSecurityException(
            SecurityException ex,
            HttpServletRequest request
    ) {
        ValidationErrorDTO errorDTO = new ValidationErrorDTO(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }
}

