package br.com.rafaelaranda.task_manager.user.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmailConverter implements AttributeConverter<Email, String> {

 @Override
    public String convertToDatabaseColumn(Email email) {
        return (email == null) ? null : email.getValue();
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? null : new Email(dbData);
    }
}
