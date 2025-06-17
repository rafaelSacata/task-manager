package br.com.rafaelaranda.task_manager.user.vo;

import java.util.Objects;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class Password {

    private static final Argon2 ARGON2 = Argon2Factory.create();

    private final String hashedPassword;

    protected Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static Password fromPlainText(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("The password cannot be null or blank");
        }

        String hash = ARGON2.hash(2, 65536, 1, plainTextPassword.toCharArray());
        return new Password(hash);
    }

    public boolean matches(String plainTextPassword) {
        return ARGON2.verify(this.hashedPassword, plainTextPassword.toCharArray());
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
        if (!(o instanceof Password)) return false;
        Password password = (Password) o;
        return Objects.equals(hashedPassword, password.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedPassword);
    }
}


