package br.com.rafaelaranda.task_manager.user.validation;

import br.com.rafaelaranda.task_manager.user.service.UserService;
import br.com.rafaelaranda.task_manager.user.vo.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, Email> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {

    }

    @Override
    public boolean isValid(Email email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }
        return !userService.existsByEmail(email);
    }
}
