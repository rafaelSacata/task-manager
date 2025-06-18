package br.com.rafaelaranda.task_manager.user.vo;

import java.util.Objects;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Password {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private final String hashedPassword;

    protected Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static Password fromPlainText(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("The password cannot be null or blank");
        }

        String hash = ENCODER.encode(plainTextPassword);
        return new Password(hash);
    }

    public boolean matches(String plainTextPassword) {
        return ENCODER.matches(plainTextPassword, this.hashedPassword);
    }

    public String getHashed() {
        return hashedPassword;
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password password)) return false;
        return Objects.equals(hashedPassword, password.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedPassword);
    }
}