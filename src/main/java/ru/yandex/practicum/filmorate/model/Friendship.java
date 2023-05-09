package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private final long userId;
    private final long friendId;
    private final boolean status;
}
