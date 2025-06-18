package br.com.rafaelaranda.task_manager.user.controller;

import br.com.rafaelaranda.task_manager.config.security.service.TokenService;
import br.com.rafaelaranda.task_manager.user.dto.AuthenticationDTO;
import br.com.rafaelaranda.task_manager.user.dto.LoginResponseDTO;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.enums.Role;
import br.com.rafaelaranda.task_manager.user.mapper.UserMapper;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;
import br.com.rafaelaranda.task_manager.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthentincationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthentincationController.class);

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final TokenService tokenService;

    private final UserMapper userMapper;

    public AuthentincationController(AuthenticationManager authenticationManager,
                                     UserService userService,
                                     TokenService tokenService,
                                     UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserEntity) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody @Valid AuthenticationDTO data) {
        UserEntity newUser = userMapper.toEntity(data);
        newUser.setRole(Role.USER);

        UserEntity savedUser = userService.saveNewUser(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }
}
