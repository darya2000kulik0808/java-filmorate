package ru.yandex.practicum.filmorate.validator.releaseDate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilmReleaseDateConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilmReleaseDateConstraint {
    String message() default "День рождения кино: 28.12.1895. Фильмов ранее этой даты не существует.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
