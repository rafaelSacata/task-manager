package br.com.rafaelaranda.task_manager.user.controller;

import br.com.rafaelaranda.task_manager.config.security.service.TokenService;
import br.com.rafaelaranda.task_manager.user.dto.AuthenticationDTO;
import br.com.rafaelaranda.task_manager.user.dto.LoginRequestDTO;
import br.com.rafaelaranda.task_manager.user.dto.LoginResponseDTO;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.enums.Role;
import br.com.rafaelaranda.task_manager.user.mapper.UserMapper;
import br.com.rafaelaranda.task_manager.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    private final UserMapper userMapper;

    public AuthenticationController(AuthenticationManager authenticationManager,
            UserService userService,
            TokenService tokenService,
            UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {

        LOGGER.info("# Receiving login request from user: {}", loginRequestDTO.email());

        try {
            final var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.email(),
                            loginRequestDTO.password()));

            final var token = tokenService.generateToken(
                    (UserDetails) auth.getPrincipal());

            LOGGER.info("# Token was generated successfully!");

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (AuthenticationException e) {

            LOGGER.error("# Authentication failed: {}", e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO("Invalid credentials"));

        } catch (Exception e) {

            LOGGER.error("# Unexpected error: {}", e.getMessage(), e);

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponseDTO("Internal error: " + e.getMessage()));

        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid AuthenticationDTO authenticationDTO) {

        LOGGER.info("# Receiving registration request from {} with email {}", authenticationDTO.name(),
                authenticationDTO.email());

        UserEntity newUser = userMapper.toEntity(authenticationDTO);
        newUser.setRole(Role.USER);

        LOGGER.info("# Defining default role for {} with email {}", authenticationDTO.name(),
                authenticationDTO.email());

        userService.saveNewUser(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        LOGGER.info("Received request to logout");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

}
