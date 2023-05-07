package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.indatabase.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final  LikesDbStorage likesDbStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("LikesDbStorage") LikesDbStorage likesDbStorage) {
        this.filmStorage = filmStorage;
        this.likesDbStorage = likesDbStorage;
    }

    public void increaseLikes(Long filmId, Long userId) {
        likesDbStorage.increaseLikes(userId, filmId);
//        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(filmId));
//
//        if(filmOptional.isPresent()){
//            Film film = filmOptional.get();
//            Collection<Long> likes = film.getLikes();
//            if (likes == null) {
//                Set<Long> setOfId = new HashSet<>();
//                setOfId.add(userId);
//                film.setLikes(setOfId);
//            } else {
//                likes.add(userId);
//            }
//        }
    }

    public void decreaseLikes(Long filmId, Long userId) {
        likesDbStorage.decreaseLikes(userId, filmId);
//        User user = userStorage.findById(userId);
//        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(filmId));
//
//        if(filmOptional.isPresent()) {
//            Film film = filmOptional.get();
//            Set<Long> setOfLikes = film.getLikes();
//            if (setOfLikes != null) {
//                setOfLikes.remove(user.getId());
//            }
//        }
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
