package ru.yandex.practicum.filmorate.validator.birthday;

import ru.yandex.practicum.filmorate.validator.descriptionLength.FilmDescriptionLengthConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserBirthdayConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserBirthdayConstraint {

    String message() default "Неправильная дата рождения, если вы человек из будущего, то заполняйте поле в будущем," +
            " а не сейчас.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
