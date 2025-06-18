package br.com.rafaelaranda.task_manager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
    
    @NotBlank(message = "Name cannot be blank")
    String name, 
    
    @Email(message = "The e-mail must be valid")
    @NotBlank(message = "Email cannot be blank")
    String email, 
    
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must have at least 6 characters")
    String password
) {}
