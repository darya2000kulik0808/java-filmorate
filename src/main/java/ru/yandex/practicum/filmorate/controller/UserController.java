package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        log.debug("Получен GET-запрос на получение пользователя с id: {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Получен GET-запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен POST-запрос на добавление пользователя: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Получен PUT-запрос на обновление пользователя: {}", user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен PUT-запрос на добавление пользователя с id - {}, в друзья к пользователю с id - {}",
                friendId, id);
        userService.addFriend(id, friendId);
        return "Добавлен новый друг: " + userService.getById(friendId).getName();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен DELETE-запрос на удаление пользователя с id - {}, из друзей пользователя с id - {}",
                friendId, id);
        userService.deleteFriend(id, friendId);
        return "Удален друг: " + userService.getById(friendId).getName();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendList(@PathVariable long id) {
        log.debug("Получен GET-запрос на получение списка друзей пользователя с id: {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Получен GET-запрос на получение списка общих друзей пользователя с id: {} - и пользователя с id: {}",
                id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
