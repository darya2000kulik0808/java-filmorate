package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
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
        Film film = filmStorage.findById(filmId);
        Collection<Long> likes = film.getLikes();
        if (likes == null) {
            Set<Long> setOfId = new HashSet<>();
            setOfId.add(userId);
            film.setLikes(setOfId);
        } else {
            likes.add(userId);
        }
    }

    public void decreaseLikes(Long filmId, Long userId) {
        User user = userStorage.findById(userId);
        Film film = filmStorage.findById(filmId);
        Set<Long> setOfLikes = film.getLikes();
        if (setOfLikes != null) {
            setOfLikes.remove(user.getId());
        }
    }

    public Collection<Film> findSomeFilmsByLikes(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparing((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
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
