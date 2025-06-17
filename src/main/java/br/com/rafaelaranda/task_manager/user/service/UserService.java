package br.com.rafaelaranda.task_manager.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createNewUser() {
        return;
    }

    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }
}
