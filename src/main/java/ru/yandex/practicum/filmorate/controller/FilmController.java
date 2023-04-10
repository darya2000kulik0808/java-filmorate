package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        log.debug("Принят GET-запрос на получение фильма с id: {}.", id);
        return filmService.getById(id);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Получен GET-запрос на получение всех фильмов.");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен POST-запрос на создание фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен PUT-запрос на обновление фильма: {}", film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public String leaveLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен PUT-запрос на увеличение колличества лайков у фильма с id: {}", id);
        filmService.increaseLikes(id, userId);
        return "Лайкнули фильм.";
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен DELETE-запрос на уменьшение колличества лайков у фильма с id: {}", id);
        filmService.decreaseLikes(id, userId);
        return "Дислайкнули фильм.";
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularByLikes(@RequestParam(defaultValue = "10") int count) {
        log.debug("Получен GET-запрос на получение списка фильмов по популярнорсти.");
        return filmService.findSomeFilmsByLikes(count);
    }
}
