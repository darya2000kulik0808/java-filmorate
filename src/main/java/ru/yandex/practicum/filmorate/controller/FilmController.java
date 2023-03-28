package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 1;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлен фильм: {}", film);
            return film;
        } else {
            throw new ValidationException("Фильма с таким айди не существует.");
        }

    }
}
