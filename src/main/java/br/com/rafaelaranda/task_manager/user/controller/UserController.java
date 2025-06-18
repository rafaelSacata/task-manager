package br.com.rafaelaranda.task_manager.user.controller;

import java.util.List;

import br.com.rafaelaranda.task_manager.user.dto.AuthenticationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.mapper.UserMapper;
import br.com.rafaelaranda.task_manager.user.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<AuthenticationDTO> createUser(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDTO(userService.createUser(authenticationDTO)));
    }

}
