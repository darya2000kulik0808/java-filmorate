package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @GetMapping
    @ResponseBody
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseBody
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    @ResponseBody
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public String addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
        return "Добавлен новый друг: " + userService.getById(friendId).getName();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public String deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        return "Удален друг: " + userService.getById(friendId).getName();
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public Collection<User> getFriendList(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
