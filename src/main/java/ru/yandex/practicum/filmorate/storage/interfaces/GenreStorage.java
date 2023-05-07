package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

    void addGenresToFilm(long filmId, long genreId);
    void removeGenreForFilm(long filmId, long genreId);
    Collection<Genre> getAllGenres();
    Genre getGenreById(long genreId);
    Collection<Genre> getAllGenresForFilm(long filmId);

}
