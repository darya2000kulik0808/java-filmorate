package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private int id = 1;

    @Override
    public Collection<Film> findAll() {
        log.debug("Нашли все фильмы.");
        return films.values();
    }

    @Override
    public Film findById(long id) {
        if (films.containsKey(id)) {
            log.debug("Нашли фильм с айди {} : " + films.get(id), id);
            return films.get(id);
        } else {
            throw new ObjectNotFoundException("Фильма с таким айди не существует.");
        }
    }

    @Override
    public Film create(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлен фильм: {}", film);
            return film;
        } else {
            throw new ObjectNotFoundException("Фильма с таким айди не существует.");
        }
    }

    @Override
    public void delete(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            log.debug("Удален фильм: {}", film);
        } else {
            throw new ObjectNotFoundException("Фильма с таким айди не существует.");
        }
    }
}
