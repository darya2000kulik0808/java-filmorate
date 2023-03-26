package ru.yandex.practicum.filmorate.validator.releaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateConstraintValidator implements
        ConstraintValidator<FilmReleaseDateConstraint, LocalDate> {
    @Override
    public void initialize(FilmReleaseDateConstraint filmReleaseDate) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate,
                           ConstraintValidatorContext cxt) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}
