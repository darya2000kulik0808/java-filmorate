package ru.yandex.practicum.filmorate.storage.indatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotInsertedException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
@Qualifier("LikesDbStorage")
@Slf4j
public class LikesDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate,
                          @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    protected Collection<Long> getLikes(long filmId) throws SQLException {
        try {
            checkFilm(filmId);
            log.info("Запрос к базе данных: получение лайков для фильма.");
            String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?;";
            ResultSetWrappingSqlRowSet rowSet = (ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet(sql, filmId);
            CachedRowSet crs = (CachedRowSet) rowSet.getResultSet();
            Set<Long> likes = new HashSet<>();

            log.info("В процессе полуения...");
            while (crs.next()) {
                likes.add(rowSet.getLong("USER_ID"));
            }
            log.info("Лайки получены.");
            return likes;
        } catch (EmptyResultDataAccessException exception) {
            return new HashSet<>();
        }
    }

    public void decreaseLikes(long userId, long filmId) {
        try {
            checkUserAndFilm(userId, filmId);

            log.info("Запрос к базе данных: уменьшение (удаление) лайков для фильма.");
            String sql = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?;";
            boolean deleted = jdbcTemplate.update(sql, userId, filmId) > 0;
            log.info("Лайк удален.");
        } catch (SQLException | DataAccessException exception) {
            throw new ObjectNotFoundException("Удалить лайк не получилось.");
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(exception.getMessage());
        }
    }

    public void increaseLikes(long userId, long filmId) {
        try {
            checkUserAndFilm(userId, filmId);

            log.info("Запрос к базе данных: увеличение (добавление) лайков для фильма.");
            String sql = "INSERT INTO LIKES(USER_ID, FILM_ID) VALUES (?, ?);";
            jdbcTemplate.update(sql, userId, filmId);
            log.info("Лайк поставлен.");
        } catch (SQLException | DataAccessException exception) {
            throw new NotInsertedException("Не получилось добавить лайк.");
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(exception.getMessage());
        }
    }

    private void checkUserAndFilm(long userId, long filmId) throws SQLException {
        User user = userStorage.findById(userId);
        checkFilm(filmId);
    }

    private void checkFilm(long filmId) throws SQLException {
        /*при попытке сделать проверку на существование фильма по аналогии с пользовтелем
            возникает циклическая зависимость*/

        String sqlFilm = "SELECT * FROM FILM WHERE FILM.FILM_ID = ?;";
        ResultSetWrappingSqlRowSet rowSet1 =
                (ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet(
                        sqlFilm,
                        filmId);
        CachedRowSet crsFilm = (CachedRowSet) rowSet1.getResultSet();
        if (!crsFilm.next()) {
            throw new ObjectNotFoundException("Фильма с айди " + filmId + " не существует.");
        }
    }
}
