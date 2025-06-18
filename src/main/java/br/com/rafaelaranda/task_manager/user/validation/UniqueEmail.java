package br.com.rafaelaranda.task_manager.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER }) // Onde a anotação pode ser usada
@Retention(RetentionPolicy.RUNTIME) // A anotação estará disponível em tempo de execução
@Constraint(validatedBy = UniqueEmailValidator.class) // Classe que implementa a lógica de validação
public @interface UniqueEmail {

    String message() default "Email already in use!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}