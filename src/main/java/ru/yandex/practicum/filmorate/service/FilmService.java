package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void increaseLikes(Long filmId, Long userId) {
        Optional<Collection<Long>> optional = Optional.ofNullable(filmStorage.findById(filmId).getLikes());
        if (optional.isEmpty()) {
            Set<Long> setOfId = new HashSet<>();
            setOfId.add(userId);
            filmStorage.findById(filmId).setLikes(setOfId);
        } else {
            filmStorage.findById(filmId).getLikes().add(userId);
        }
    }

    public void decreaseLikes(Long filmId, Long userId) {
        User user = userStorage.findById(userId);
        Optional<Collection<Long>> optional = Optional.ofNullable(filmStorage.findById(filmId).getLikes());
        if (optional.isPresent()) {
            filmStorage.findById(filmId).getLikes().remove(user.getId());
        }
    }

    public Collection<Film> findSomeFilmsByLikes(int count) {
        Collection<Film> films = filmStorage.findAll();
        Collection<Film> filmsWithLikes = new HashSet<>();
        Collection<Film> filmsWithoutLikes = new HashSet<>();

        for (Film film : films) {
            Optional<Set<Long>> optional = Optional.ofNullable(film.getLikes());
            if (optional.isPresent()) {
                filmsWithLikes.add(film);
            } else {
                filmsWithoutLikes.add(film);
            }
        }

        if (!filmsWithLikes.isEmpty()) {
            Collection<Film> allFilms = filmsWithLikes
                    .stream()
                    .sorted(Comparator.comparing((Film f) -> f.getLikes().size()))
                    .collect(Collectors.toCollection(ArrayList::new));
            allFilms.addAll(filmsWithoutLikes);
            return allFilms
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return filmsWithoutLikes
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    public Film getById(long id) {
        return filmStorage.findById(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }
}
