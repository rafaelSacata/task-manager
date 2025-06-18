package br.com.rafaelaranda.task_manager.user.vo;

import java.util.Objects;

public class Email {
    private final String value;

    protected Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (!value.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) {
            throw new IllegalArgumentException("Email inválido: " + value);
        }
        return new Email(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
