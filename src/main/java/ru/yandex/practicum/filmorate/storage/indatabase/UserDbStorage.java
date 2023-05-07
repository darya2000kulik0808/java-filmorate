package ru.yandex.practicum.filmorate.storage.indatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotInsertedException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FriendshipDbStorage friendshipDbStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendshipDbStorage friendshipDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    @Override
    public User create(User user) {
        try {
            Optional<String> optionalUserName = Optional.ofNullable(user.getName());

            if (optionalUserName.isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }

            log.info("Запрос к базе данных: добавление пользователя.");
            String sql = "INSERT INTO FILMORATE_USER(USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY)" +
                    "VALUES(?, ?, ?, ?);";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);

            log.info("Получаем айди добавленного пользователя...");
            long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(userId);

            log.info("Добавили пользователя {}", user);
            return user;
        } catch (DataAccessException exception) {
            throw new NotInsertedException("Не удалось добавить пользователя.");
        }
    }

    @Override
    public User update(User user) {
        try {
            log.info("Запрос к базе данных: обновление пользователя.");
            String sql = "UPDATE FILMORATE_USER " +
                    "SET USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?," +
                    "USER_BIRTHDAY = ?" +
                    "WHERE USER_ID = ?;";
            int rows = jdbcTemplate.update(sql,
                    user.getEmail(), user.getLogin(), user.getName(),
                    user.getBirthday(), user.getId());
            if(rows != 0){
                log.debug("Обновлен пользователь: {}", user);
                return user;
            } else {
                throw new ObjectNotFoundException("Пользовтаель не был найден или обновлен.");
            }
        } catch (DataAccessException exception) {
            throw new ObjectNotFoundException("Не удалось обновить пользователя.");
        }
    }

    @Override
    public void delete(User user) {
        try {
            log.info("Запрос к базе данных: удаление пользователя.");
            String sql = "DELETE FROM FILMORATE_USER WHERE USER_ID = ?;";
            jdbcTemplate.update(sql, user.getId());
            log.info("Запись удалена");
            log.info("Удален пользователь: {}", user);
        } catch (DataAccessException exception) {
            throw new ObjectNotFoundException("Пользователь не существует.");
        }
    }

    @Override
    public Collection<User> findAll() {
        try {
            log.info("Запрос к базе данных: найти всех пользователей.");
            String sql = "SELECT * FROM FILMORATE_USER;";
            Collection<User> users = jdbcTemplate.query(sql, this::makeUser);
            log.info("Нашли всех пользователей.");
            return users;
        } catch (EmptyResultDataAccessException exception) {
            log.info("По пользователям ичего не нашли.");
            throw new ObjectNotFoundException("Объекты не найдены.");
        }
    }

    @Override
    public User findById(long id) {
        try {
            log.info("Запрос к базе данных: найти пользователя по айди.");
            String sql = "SELECT * FROM FILMORATE_USER WHERE USER_ID = ?;";
            User user = jdbcTemplate.queryForObject(sql, this::makeUser, id);
            log.info("Нашли пользователя с айди {} : " + user, id);
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Пользователя с таким айди не существует.");
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("USER_ID");
        String email = resultSet.getString("USER_EMAIL");
        String login = resultSet.getString("USER_LOGIN");
        String name = resultSet.getString("USER_NAME");
        LocalDate birthday = resultSet.getDate("USER_BIRTHDAY").toLocalDate();
        Set<Long> friends = friendshipDbStorage.filterFriends(id);
        return new User(id, email, login, name, birthday, friends);
    }
}
