package br.com.rafaelaranda.task_manager.config.security.service;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import br.com.rafaelaranda.task_manager.user.repository.UserRepository;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("# Searching user with email: {}", email);
        Email emailVO = Email.of(email);
        UserEntity userEntity = userRepository.findByEmail(emailVO);
        if (userEntity == null) {
            LOGGER.error("# User NOT FOUND with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        LOGGER.info("# User found! => [ USEREMAIL: {} ]", userEntity.getEmail());
        List<SimpleGrantedAuthority> authorities = userEntity.getRole() != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()))
                : Collections.emptyList();
        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail().getValue(),
                userEntity.getPassword().getHashed(),
                authorities
        );
    }
}