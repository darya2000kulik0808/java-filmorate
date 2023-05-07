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

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected Collection<Long> getLikes(long filmId) throws SQLException {
        try {
            if(filmId < 0){
                throw new ObjectNotFoundException("Фильмов с отрицательным айди не существует.");
            } else {
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
            }
        } catch (EmptyResultDataAccessException exception) {
            return new HashSet<>();
        }
    }

    public void decreaseLikes(long userId, long filmId) {
        try {
            if(userId < 0 || filmId < 0){
                throw new ObjectNotFoundException("Фильмов с отрицательным айди не существует.");
            } else {
                log.info("Запрос к базе данных: уменьшение (удаление) лайков для фильма.");
                String sql = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?;";
                boolean deleted = jdbcTemplate.update(sql, userId, filmId) > 0;
                log.info("Лайк удален.");
            }
        } catch (DataAccessException exception) {
            throw new ObjectNotFoundException("Удалить лайк не получилось.");
        }
    }

    public void increaseLikes(long userId, long filmId) {
        try {
            if(userId < 0 || filmId < 0){
                throw new ObjectNotFoundException("Фильмов с отрицательным айди не существует.");
            } else {
                log.info("Запрос к базе данных: увеличение (добавление) лайков для фильма.");
                String sql = "INSERT INTO LIKES(USER_ID, FILM_ID) VALUES (?, ?);";
                jdbcTemplate.update(sql, userId, filmId);
                log.info("Лайк поставлен.");
            }
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не получилось добавить лайк.");
        }
    }
}
