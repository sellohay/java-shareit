package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public User getUserById(Long id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userStorage.findById(id).get();
    }

    @Override
    public User createUser(User user) {
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new SameEmailException("Пользователь с почтой \"" + user.getEmail() + "\" уже есть");
        }

        return userStorage.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        Optional<User> oldUserOpt = userStorage.findById(id);
        if (oldUserOpt.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        User oldUser = oldUserOpt.get();
        if (user.getEmail() != null) {
            if (!Objects.equals(oldUser.getEmail(), user.getEmail()) && userStorage.existsByEmail(user.getEmail())) {
                throw new SameEmailException("Пользователь с почтой \"" + user.getEmail() + "\" уже есть");
            }
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return userStorage.save(oldUser);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }

    @Override
    public void checkUserExists(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

}
