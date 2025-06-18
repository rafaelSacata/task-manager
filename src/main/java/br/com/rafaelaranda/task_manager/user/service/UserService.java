package br.com.rafaelaranda.task_manager.user.service;

import java.util.List;

import br.com.rafaelaranda.task_manager.user.dto.AuthenticationDTO;
import br.com.rafaelaranda.task_manager.user.enums.Role;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.mapper.UserMapper;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByEmail(Email userEmail) {
        return userRepository.existsByEmail(userEmail);
    }

    public UserEntity saveNewUser(UserEntity user) {
        if (user.getRole() != Role.USER) {
            throw new RuntimeException(String.format("Forbidden role detected for user %s", user.getName()));
        }
        return userRepository.save(user);
    }
}
