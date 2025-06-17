package br.com.rafaelaranda.task_manager.user.vo;

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Email other = (Email) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
