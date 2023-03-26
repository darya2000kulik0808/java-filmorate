package ru.yandex.practicum.filmorate.validator.descriptionLength;

import javax.validation.Payload;
import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilmDescriptionLengthConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilmDescriptionLengthConstraint {

    String message() default "Длина описания не может быть более 200 символов.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
