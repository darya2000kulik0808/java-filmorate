package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Film getById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @GetMapping
    @ResponseBody
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseBody
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    @ResponseBody
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }


    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public String leaveLike(@PathVariable long id, @PathVariable long userId) {
        filmService.increaseLikes(id, userId);
        return "Лайкнули фильм.";
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseBody
    public String removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.decreaseLikes(id, userId);
        return "Дислайкнули фильм.";
    }

    @GetMapping("/popular")
    @ResponseBody
    public Collection<Film> getMostPopularByLikes(@RequestParam Optional<Integer> count) {
        return filmService.findSomeFilmsByLikes(count.orElse(10));
    }
}
