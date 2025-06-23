package br.com.rafaelaranda.task_manager.task.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.rafaelaranda.task_manager.reminder.enums.ReminderInterval;
import br.com.rafaelaranda.task_manager.user.entity.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "task_id", updatable = false, nullable = false)
    private UUID taskId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "completition_date", nullable = true)
    private LocalDateTime completionDate;

    @Column(name = "completion_note", length = 200)
    private String completionNote;

    @Column(name = "has_reminders", nullable = false)
    private boolean hasReminders;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_interval")
    private ReminderInterval reminderInterval;

    @Column(name = "last_reminder_sent")
    private LocalDateTime lastReminderSent;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity userEntity) {
        this.user = userEntity;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public boolean isHasReminders() {
        return hasReminders;
    }

    public void setHasReminders(boolean hasReminders) {
        this.hasReminders = hasReminders;
    }

    public ReminderInterval getReminderInterval() {
        return reminderInterval;
    }

    public void setReminderInterval(ReminderInterval reminderInterval) {
        this.reminderInterval = reminderInterval;
    }

    public LocalDateTime getLastReminderSent() {
        return lastReminderSent;
    }

    public void setLastReminderSent(LocalDateTime lastReminderSent) {
        this.lastReminderSent = lastReminderSent;
    }
}