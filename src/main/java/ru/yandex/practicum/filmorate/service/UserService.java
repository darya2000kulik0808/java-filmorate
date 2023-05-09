package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.indatabase.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FriendshipDbStorage") FriendshipDbStorage friendshipDbStorage) {
        this.userStorage = userStorage;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    public void addFriend(long userId, long friendId) {
        friendshipDbStorage.addFriendship(userId, friendId);
//        User user = userStorage.findById(userId);
//        User friend = userStorage.findById(friendId);
//
//        if (user.getFriends() == null) {
//            Set<Long> beginningOfFriendList1 = new HashSet<>();
//            beginningOfFriendList1.add(friendId);
//            user.setFriends(beginningOfFriendList1);
//        } else {
//            user.getFriends().add(friendId);
//        }
//
//        if (friend.getFriends() == null) {
//            Set<Long> beginningOfFriendList2 = new HashSet<>();
//            beginningOfFriendList2.add(userId);
//            friend.setFriends(beginningOfFriendList2);
//        } else {
//            friend.getFriends().add(userId);
//        }
    }

    public void deleteFriend(long userId, long friendId) {
        friendshipDbStorage.deleteFriendship(userId, friendId);
//        User user = userStorage.findById(userId);
//        User friend = userStorage.findById(friendId);
//
//        if (user.getFriends() != null && friend.getFriends() != null) {
//            user.getFriends().remove(friendId);
//            friend.getFriends().remove(userId);
//        } else {
//            throw new ObjectNotFoundException("Список друзей пуст, некого удалять.");
//        }
    }

    public Collection<User> getAllFriends(long id) {
        Set<User> usersFriends = new HashSet<>();
        User user = userStorage.findById(id);
        for (Long idFriend : user.getFriends()) {
            usersFriends.add(userStorage.findById(idFriend));
        }

        return usersFriends
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user1 = userStorage.findById(userId);
        User user2 = userStorage.findById(otherUserId);

        if (user1.getFriends() != null && user2.getFriends() != null) {
            Set<User> users = new HashSet<>();
            Set<Long> usersCommon = user1
                    .getFriends()
                    .stream()
                    .filter(user2.getFriends()::contains)
                    .collect(Collectors.toSet());

            for (Long commonId : usersCommon) {
                users.add(userStorage.findById(commonId));
            }

            return users;
        } else {
            return new HashSet<>();
        }
    }

    public User getById(long id) {
        return userStorage.findById(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }
}
