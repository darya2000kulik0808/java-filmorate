package ru.yandex.practicum.filmorate.validator.descriptionLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FilmDescriptionLengthConstraintValidator implements
        ConstraintValidator<FilmDescriptionLengthConstraint, String> {

    @Override
    public void initialize(FilmDescriptionLengthConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length() < 200;
    }
}
