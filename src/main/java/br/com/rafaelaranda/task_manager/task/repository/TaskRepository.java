package br.com.rafaelaranda.task_manager.task.repository;

import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByUser(UserEntity user);
    List<TaskEntity> findByHasRemindersTrueAndCompletedFalse();
    @Query("SELECT t FROM TaskEntity t WHERE t.user = :user AND t.hasReminders = true AND t.completed = false " +
           "AND (t.lastReminderSent IS NULL OR t.lastReminderSent <= :cutoffTime)")
    List<TaskEntity> findPendingReminders(UserEntity user, LocalDateTime cutoffTime);
}