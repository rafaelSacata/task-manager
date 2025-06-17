package br.com.rafaelaranda.task_manager.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rafaelaranda.task_manager.user.dto.UserDTO;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.mapper.UserMapper;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    public UserEntity createUser(UserDTO userDTO) {
        UserEntity user = userMapper.toEntity(userDTO);
        return userRepository.save(user);
    }
}
