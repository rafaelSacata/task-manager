package br.com.rafaelaranda.task_manager.user.entity;

import java.util.List;
import java.util.UUID;

import br.com.rafaelaranda.task_manager.task.entity.TaskEntity;
import br.com.rafaelaranda.task_manager.user.enums.Role;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import br.com.rafaelaranda.task_manager.user.vo.EmailConverter;
import br.com.rafaelaranda.task_manager.user.vo.Password;
import br.com.rafaelaranda.task_manager.user.vo.PasswordConverter;
import jakarta.persistence.*;

import static jakarta.persistence.EnumType.STRING;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(length = 200)
    private String name;

    @Convert(converter = EmailConverter.class)
    @Column(unique = true, nullable = false)
    private Email email;

    @Convert(converter = PasswordConverter.class)
    private Password password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks;

    @Enumerated(STRING)
    private Role role;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
