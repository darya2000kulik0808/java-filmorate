package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        Optional<String> optionalUserName = Optional.ofNullable(user.getName());

        if(!optionalUserName.isPresent()){
            user.setName(user.getLogin());
        }

        user.setId(id);
        users.put(user.getId(), user);
        id++;
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if(users.containsKey(user.getId()))
        {
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь: {}", user);
            return user;
        }else {
            throw new ValidationException("Пользователя с таким айди не существует.");
        }
    }
}
