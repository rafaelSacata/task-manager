package br.com.rafaelaranda.task_manager.user.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PasswordConverter implements AttributeConverter<Password, String> {

    @Override
    public String convertToDatabaseColumn(Password password) {
        if (password == null) return null;
        return password.getHashed();
    }

    @Override
    public Password convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        return new Password(dbData);
    }
}