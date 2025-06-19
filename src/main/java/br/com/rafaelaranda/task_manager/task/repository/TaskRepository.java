package br.com.rafaelaranda.task_manager.task.repository;

import java.util.List;
import java.util.UUID;

import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUser(UserEntity user);
}
