package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        if (!userStorage.checkUserExists(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userStorage.getUserById(id);
    }

    @Override
    public User createUser(User user) {
        if (userStorage.checkEmailExists(user.getEmail())) {
            throw new SameEmailException("Пользователь с почтой \"" + user.getEmail() + "\" уже есть");
        }
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User oldUser = userStorage.getUserById(id);
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (user.getEmail() != null) {
            if (!Objects.equals(oldUser.getEmail(), user.getEmail()) && userStorage.checkEmailExists(user.getEmail())) {
                throw new SameEmailException("Пользователь с почтой \"" + user.getEmail() + "\" уже есть");
            }
        }
        user.setId(id);
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public boolean userExists(Long userId) {
        return userStorage.checkUserExists(userId);
    }

}
