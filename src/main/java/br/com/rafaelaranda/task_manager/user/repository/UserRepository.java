package br.com.rafaelaranda.task_manager.user.repository;

import java.util.UUID;

import br.com.rafaelaranda.task_manager.user.vo.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>{
    UserDetails findOneByEmail(String userEmail);
    boolean existsByEmail(Email email);
}
