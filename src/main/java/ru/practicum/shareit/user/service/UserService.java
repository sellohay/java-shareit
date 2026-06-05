package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    boolean userExists(Long userId);
}
