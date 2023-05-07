package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Genre {
    private final long id;
    @NotBlank(message = "Название не может быть пустым.")
    private final String name;
}
