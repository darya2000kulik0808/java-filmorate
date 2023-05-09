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
import ru.yandex.practicum.filmorate.model.Friendship;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
@Qualifier("FriendshipDbStorage")
@Slf4j
public class FriendshipDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected Collection<Friendship> findAllUserFriends(long userId) {
        try {
            log.info("Запрос на поиск друзей для юзера");
            String sql = "SELECT * " +
                    "FROM FRIENDSHIP AS F " +
                    "WHERE F.USER_1_ID = ? OR F.USER_2_ID = ?;";

            Collection<Friendship> friendships = jdbcTemplate.query(sql, this::makeFriendship, userId, userId);
            log.info("Нашли список друзей пользователя.");
            return friendships;
        } catch (EmptyResultDataAccessException exception) {
            return new HashSet<>();
        }
    }

    public void addFriendship(long userId, long friendId) {
        try {
            if (userId < 0 || friendId < 0) {
                throw new ObjectNotFoundException("Пользователей с отрицательным айди не существует.");
            }
            if (userId == friendId) {
                throw new NotInsertedException("Нельзя добавить самого себя в друзья.");
            } else {
                String sqlCheckExistingFriendship = "SELECT * " +
                        "FROM FRIENDSHIP AS F " +
                        "WHERE (F.USER_1_ID = ? AND F.USER_2_ID = ?);";

                ResultSetWrappingSqlRowSet rowSet1 =
                        (ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet(
                                sqlCheckExistingFriendship,
                                userId, friendId);
                CachedRowSet crs1 = (CachedRowSet) rowSet1.getResultSet();

                ResultSetWrappingSqlRowSet rowSet2 =
                        (ResultSetWrappingSqlRowSet) jdbcTemplate.queryForRowSet(
                                sqlCheckExistingFriendship,
                                friendId, userId);
                CachedRowSet crs2 = (CachedRowSet) rowSet2.getResultSet();

                if (crs1.next()) {
                    approveFriendship(userId, friendId);
                } else if (crs2.next()) {
                    approveFriendship(friendId, userId);
                } else {
                    log.info("Запрос на добавление дружбы.");
                    String sql = "INSERT INTO FRIENDSHIP(USER_1_ID, USER_2_ID, FRIENDSHIP_STATUS) " +
                            "VALUES(?, ?, ?);";

                    jdbcTemplate.update(sql, userId, friendId, false);
                    log.info("Добавили запрос на дружбу для пользователя с айди {}" +
                            " от пользователя с айди {}", friendId, userId);
                }
            }
        } catch (SQLException | DataAccessException exception) {
            throw new NotInsertedException("Не удалось добавить дружбу.");
        }
    }

    public void approveFriendship(long userId, long friendId) {
        try {
            log.info("Запрос на подтверждение дружбы.");
            String sql = "UPDATE FRIENDSHIP SET FRIENDSHIP_STATUS = TRUE " +
                    "WHERE USER_1_ID = ? AND USER_2_ID = ?;";

            jdbcTemplate.update(sql, userId, friendId);
            log.info("Обновили дружбу для пользователей с айди {}" +
                    " и {}", friendId, userId);
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не удалось обновить статус дружбы.");
        }
    }

    public void deleteFriendship(long userId, long friendId) {
        try {
            log.info("Запрос на удаление дружбы.");
            String sql = "DELETE FROM FRIENDSHIP " +
                    "WHERE USER_1_ID = ? AND USER_2_ID = ?;";

            jdbcTemplate.update(sql, userId, friendId);
            log.info("Удалили дружбу пользователей с айди {}" +
                    " и {}", friendId, userId);
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не удалось удалить дружбу.");
        }
    }

    protected Set<Long> filterFriends(long userId) {
        Collection<Friendship> friendships = findAllUserFriends(userId);
        Set<Long> friends = new HashSet<>();

        if (friendships != null) {
            log.info("Выбираем нужные айди в список дурзей.");
            for (Friendship friendship : friendships) {
                if (friendship.getFriendId() == userId && friendship.isStatus()) {
                    friends.add(friendship.getUserId());
                } else if (friendship.getUserId() == userId) {
                    friends.add(friendship.getFriendId());
                }
            }
            log.info("Список айди друзей сделан.");
        }
        return friends;
    }

    private Friendship makeFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        long userId = resultSet.getLong("USER_1_ID");
        long friendId = resultSet.getLong("USER_2_ID");
        boolean status = resultSet.getBoolean("FRIENDSHIP_STATUS");
        return new Friendship(userId, friendId, status);
    }
}
