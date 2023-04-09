package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId){
        Optional<Set<Long>> optionalUser = Optional.ofNullable(userStorage.findById(userId).getFriends());
        Optional<Set<Long>> optionalFriend = Optional.ofNullable(userStorage.findById(friendId).getFriends());

        if (optionalUser.isEmpty()) {

            Set<Long> beginningOfFriendList1 = new HashSet<>();
            beginningOfFriendList1.add(friendId);
            userStorage.findById(userId).setFriends(beginningOfFriendList1);
        }else {
            userStorage.findById(userId).getFriends().add(friendId);
        }

        if(optionalFriend.isEmpty()){
            Set<Long> beginningOfFriendList2 = new HashSet<>();
            beginningOfFriendList2.add(userId);
            userStorage.findById(friendId).setFriends(beginningOfFriendList2);
        }else {
            userStorage.findById(friendId).getFriends().add(userId);
        }
    }

    public void deleteFriend(long userId, long friendId){
        Optional<Set<Long>> optionalUser = Optional.ofNullable(userStorage.findById(userId).getFriends());
        Optional<Set<Long>> optionalFriend = Optional.ofNullable(userStorage.findById(friendId).getFriends());

        if (optionalUser.isEmpty() && optionalFriend.isEmpty()){
            throw new ObjectNotFoundException("Список друзей пуст, некого удалять.");
        }else {
            userStorage.findById(userId).getFriends().remove(friendId);
            userStorage.findById(friendId).getFriends().remove(userId);
        }
    }

    public Collection<User> getAllFriends(long id){
        Set<User> usersFriends = new HashSet<>();
        User user = userStorage.findById(id);
        for(Long idFriend : user.getFriends()){
            usersFriends.add(userStorage.findById(idFriend));
        }

        return  usersFriends
                .stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId){
        Optional<Set<Long>> optionalUser = Optional.ofNullable(userStorage.findById(userId).getFriends());
        Optional<Set<Long>> optionalOther = Optional.ofNullable(userStorage.findById(otherUserId).getFriends());
        if(optionalUser.isPresent() && optionalOther.isPresent()){
            Set<User> users = new HashSet<>();
            Set<Long> usersCommon = userStorage
                                        .findById(userId)
                                        .getFriends()
                                        .stream()
                                        .filter(userStorage.findById(otherUserId).getFriends()::contains)
                                        .collect(Collectors.toSet());

            for (Long commonId: usersCommon) {
                users.add(userStorage.findById(commonId));
            }

            return users;
        }else {
            return new HashSet<>();
        }
    }

    public User getById(long id){
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
