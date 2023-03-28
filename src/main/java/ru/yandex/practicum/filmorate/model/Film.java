package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.descriptionLength.FilmDescriptionLengthConstraint;
import ru.yandex.practicum.filmorate.validator.releaseDate.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание не может быть пустым.")
    @FilmDescriptionLengthConstraint
    private String description;
    @NotNull(message = "Дата выхода обязательна к заполнению.")
    @FilmReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive(message = "Длительность не можеть быть меньше или равна нулю.")
    private int duration;
}
