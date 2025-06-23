package br.com.rafaelaranda.task_manager.user.service;

import br.com.rafaelaranda.task_manager.user.enums.Role;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByEmail(Email userEmail) {
        return userRepository.existsByEmail(userEmail);
    }

    public UserEntity saveNewUser(UserEntity user) {
        if (user.getRole() != Role.USER) {
            LOGGER.error("# No user created by the registration endpoint can have the administrator role!");
            throw new RuntimeException(
                    String.format("### Attempt to include user with prohibited role %s", user.getName())
            );
        }
        LOGGER.info("# User created successfully!");
        return userRepository.save(user);
    }

    public UserEntity getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            LOGGER.error("No authenticated user found");
            throw new SecurityException("No authenticated user found");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        UserEntity user = userRepository.findByEmail(Email.of(email));
        if (user == null) {
            LOGGER.error("User not found: {}", email);
            throw new EntityNotFoundException("User not found: " + email);
        }
        return user;
    }
}
