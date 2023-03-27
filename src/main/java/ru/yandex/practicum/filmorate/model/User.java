package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.birthday.UserBirthdayConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotBlank(message = "Поле эектронной почты не может быть пустым!")
    @Email(message = "Неверный формат электронной почты!")
    private String email;
    @NotBlank(message = "Поле \"логин\" не может быть пустым.")
    private String login;
    private String name;
    @NotNull(message = "День рождения обязателен к заполнению.")
    @UserBirthdayConstraint
    private LocalDate birthday;
}
