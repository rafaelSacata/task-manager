package br.com.rafaelaranda.task_manager.user.entity;

import java.util.List;
import java.util.UUID;

import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import br.com.rafaelaranda.task_manager.user.vo.Password;
import br.com.rafaelaranda.task_manager.user.vo.PasswordConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(length = 200)
    private String name;

    private Email email;

    @Convert(converter = PasswordConverter.class)
    private Password password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskEntity> taks) {
        this.tasks = taks;
    }

    public UUID getUserId() {
        return userId;
    }

    
}
