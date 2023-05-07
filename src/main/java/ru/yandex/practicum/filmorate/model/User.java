package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.birthday.UserBirthdayConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private long id;
    @NotBlank(message = "Поле эектронной почты не может быть пустым!")
    @Email(message = "Неверный формат электронной почты!")
    private String email;
    @NotBlank(message = "Поле \"логин\" не может быть пустым.")
    private String login;
    private String name;
    @NotNull(message = "День рождения обязателен к заполнению.")
    @UserBirthdayConstraint
    private LocalDate birthday;
    private Set<Long> friends;

    public User(long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }
}
