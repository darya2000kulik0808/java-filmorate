package ru.yandex.practicum.filmorate.storage.indatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotInsertedException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@Qualifier("GenreDbStorage")
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addGenresToFilm(long filmId, long genreId) {
        try {
            log.info("Запрос к базе данных: добавление жанров для фильма.");
            String sql = "INSERT INTO GENRE_FILM(GF_FILM_ID, GF_GENRE_ID) " +
                    "VALUES(?, ?);";
            int rows = jdbcTemplate.update(sql, filmId, genreId);
            if (rows != 0) {
                log.debug("Добавлены жанры для фильма с айди {}", filmId);
            } else {
                throw new ObjectNotFoundException("Жанры для фильма не были найдены или обновлены.");
            }
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не удалось добавить жанры для фильма.");
        }
    }

    public void removeGenreForFilm(long filmId, long genreId) {
        try {
            log.info("Запрос к базе данных: удаление жанра для фильма.");
            String sql = "DELETE FROM GENRE_FILM WHERE GF_FILM_ID = ? AND GF_GENRE_ID = ?;";
            int rows = jdbcTemplate.update(sql, filmId, genreId);
            if (rows != 0) {
                log.debug("Удален жанр для фильма с айди {}", filmId);
            } else {
                throw new ObjectNotFoundException("Жанры для фильма не были найдены или удалены.");
            }
        } catch (DataAccessException exception) {
            throw new ObjectNotFoundException("Не удалось удалить жанры для фильма.");
        }
    }

    public Collection<Genre> getAllGenres() {
        try {
            log.info("Запрос к базе данных: получение всех жанров.");
            String sql = "SELECT * FROM GENRE;";
            Collection<Genre> genres = jdbcTemplate.query(sql, this::makeGenre);
            log.info("Получили список жанров.");
            return genres;
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Жанры не найдены.");
        }

    }

    public Genre getGenreById(long genreId) {
        try {
            log.info("Запрос к базе данных: получение жанра по айди.");
            String sql = "SELECT * FROM GENRE WHERE GENRE.GENRE_ID = ?;";
            Genre genre = jdbcTemplate.queryForObject(sql, this::makeGenre, genreId);
            log.info("Нашли жанр с айди {}.", genreId);
            return genre;
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Жанр с айди " + genreId + " не найден.");
        }
    }

    public Collection<Genre> getAllGenresForFilm(long filmId) {
        try {
            log.info("Запрос к базе данных: получение жанров для фильма.");
            String sql = "SELECT G.GENRE_ID, G.GENRE_NAME " +
                    "FROM GENRE_FILM AS GF " +
                    "INNER JOIN GENRE AS G ON GF.GF_GENRE_ID = G.GENRE_ID " +
                    "WHERE GF.GF_FILM_ID = ?;";
            Collection<Genre> genres = jdbcTemplate.query(sql, (this::makeGenre), filmId);
            log.info("Получили список жанров для фильма.");
            return genres;
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Не найдены жанры для фильма с айди " + filmId);
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("GENRE_ID");
        String name = resultSet.getString("GENRE_NAME");
        return new Genre(id, name);
    }
}
