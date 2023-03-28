package ru.yandex.practicum.filmorate.validator.birthday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class UserBirthdayConstraintValidator implements
        ConstraintValidator<UserBirthdayConstraint, LocalDate> {
    @Override
    public void initialize(UserBirthdayConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isBefore(LocalDate.now());
    }
}
