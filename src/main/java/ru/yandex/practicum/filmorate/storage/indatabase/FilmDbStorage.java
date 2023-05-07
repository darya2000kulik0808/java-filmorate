package ru.yandex.practicum.filmorate.storage.indatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotInsertedException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikesDbStorage likesDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("MpaDbStorage") MpaStorage mpaStorage,
                         @Qualifier("GenreDbStorage") GenreStorage genreStorage,
                         @Qualifier("LikesDbStorage") LikesDbStorage likesDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likesDbStorage = likesDbStorage;
    }

    @Override
    public Film create(Film film) {
        try {
            log.info("Запрос к базе данных: создание фильма.");
            String sql = "INSERT INTO FILM(FILM_NAME, FILM_DESCRIPTION," +
                    " FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA_ID)" +
                    "VALUES(?, ?, ?, ?, ?);";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"FILM_ID"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setInt(4, film.getDuration());
                stmt.setLong(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);

            log.info("Получаем айди добавленного фильма...");
            long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            if (Optional.ofNullable(film.getGenres()).isPresent()) {
                log.info("Добавляем жанры...");
                film.getGenres()
                        .forEach(genre -> genreStorage.addGenresToFilm(filmId, genre.getId()));
            }

            Film createdFilm = findById(filmId);
            log.info("Добавили фильм {}", createdFilm);
            return createdFilm;
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не удалось добавить фильм.");
        }
    }

    @Override
    public Film update(Film film) {
        try {
            log.info("Запрос к базе данных: обновление фильма.");
            String sql = "UPDATE FILM " +
                    "SET FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?," +
                    "FILM_DURATION = ?, FILM_MPA_ID = ?" +
                    "WHERE FILM_ID = ?;";

            Film filmBeforeUpdate = findById(film.getId());
            if(Optional.ofNullable(filmBeforeUpdate.getGenres()).isPresent()){
                Set<Genre> diff = filmBeforeUpdate.getGenres().stream()
                        .filter(i -> !film.getGenres().contains(i))
                        .collect(Collectors.toSet());
                diff.forEach(genre -> genreStorage.removeGenreForFilm(film.getId(), genre.getId()));
            }

            Optional<List<Genre>> genres = Optional.ofNullable(film.getGenres());
            if(genres.isPresent()){
                Set<Genre> diff = genres.get().stream()
                        .filter(i -> !filmBeforeUpdate.getGenres().contains(i))
                        .collect(Collectors.toSet());
                diff.forEach(genre -> genreStorage.addGenresToFilm(film.getId(), genre.getId()));
            }

            int rows = jdbcTemplate.update(sql,
                    film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(),
                    film.getId());

            if (rows != 0) {
                Film updatedFilm = findById(film.getId());
                log.info("Обновлен фильм: {}", updatedFilm);
                return updatedFilm;
            } else {
                throw new ObjectNotFoundException("Фильм не был найден или обновлен.");
            }
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Фильма для обновления не существует.");
        }
    }

    @Override
    public void delete(Film film) {
        try {
            log.info("Запрос к базе данных: удаление фильма.");
            String sql = "DELETE FROM FILM WHERE FILM.FILM_ID = ?;";
            jdbcTemplate.update(sql, film.getId());
            log.info("Запись удалена");
            log.info("Удален фильм: {}", film);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundException("Фильм для удаления не найден.");
        }
    }

    @Override
    public Collection<Film> findAll() {
        try {
            log.info("Запрос к базе данных: найти все фильмы.");
            String sql = "SELECT * FROM FILM;";
            Collection<Film> films = jdbcTemplate.query(sql, this::makeFilm);
            log.info("Нашли все фильмы.");
            return films;
        } catch (EmptyResultDataAccessException exception) {
            log.info("Ничего не нашли.");
            throw new ObjectNotFoundException("Объекты не найдены.");
        }
    }

    @Override
    public Film findById(long id) {
        try {
            log.info("Запрос к базе данных: найти фильм по айди.");
            String sql = "SELECT * FROM FILM WHERE FILM.FILM_ID = ?;";
            Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, id);
            log.info("Нашли фильм с айди {} : " + film, id);
            return film;
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundException("Фильма с айди" + id + " не существует.");
        }
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("FILM_ID");
        String name = resultSet.getString("FILM_NAME");
        String description = resultSet.getString("FILM_DESCRIPTION");
        LocalDate releaseDate = resultSet.getDate("FILM_RELEASE_DATE").toLocalDate();
        int duration = resultSet.getInt("FILM_DURATION");
        long mpa_id = resultSet.getLong("FILM_MPA_ID");

        Mpa mpa = mpaStorage.getMpaById(mpa_id);
        List<Genre> genreList = (List<Genre>) genreStorage.getAllGenresForFilm(id);
        Set<Long> likes = (Set<Long>) likesDbStorage.getLikes(id);

        return new Film(id, name, description, releaseDate, duration, mpa, genreList, likes);
    }
}
