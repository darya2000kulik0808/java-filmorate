package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private int id = 1;

    @Override
    public Collection<User> findAll() {
        log.debug("Нашли всех юзеров.");
        return users.values();
    }

    @Override
    public User findById(long id) {
        if (users.containsKey(id)) {
            log.debug("Нашли юзера с айди {} : " + users.get(id), id);
            return users.get(id);
        } else {
            throw new ObjectNotFoundException("Пользователя с таким айди не существует.");
        }
    }

    @Override
    public User create(User user) {
        Optional<String> optionalUserName = Optional.ofNullable(user.getName());

        if (optionalUserName.isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(id);
        users.put(user.getId(), user);
        id++;
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь: {}", user);
            return user;
        } else {
            throw new ObjectNotFoundException("Пользователя с таким айди не существует.");
        }
    }

    @Override
    public void delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            log.debug("Удален пользователь: {}", user);
        } else {
            throw new ObjectNotFoundException("Пользователя с таким айди не существует.");
        }
    }
}
