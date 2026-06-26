package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void updateUser_shouldUpdateNameAndEmail() {
        User oldUser = new User();
        oldUser.setName("Старое имя");
        oldUser.setEmail("old@yandex.ru");
        em.persist(oldUser);
        em.flush();

        User updateData = new User();
        updateData.setName("Новое имя");
        updateData.setEmail("new@yandex.ru");

        User updatedUser = userService.updateUser(oldUser.getId(), updateData);

        assertThat(updatedUser.getId()).isEqualTo(oldUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(updateData.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(updateData.getEmail());

        User userFromDb = em.find(User.class, oldUser.getId());
        assertThat(userFromDb.getName()).isEqualTo("Новое имя");
        assertThat(userFromDb.getEmail()).isEqualTo("new@yandex.ru");
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowsNotFoundException() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createUser_whenEmailAlreadyExists_thenThrowsSameEmailException() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("duplicate@email.com");
        em.persist(user1);
        em.flush();

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("duplicate@email.com");

        assertThatThrownBy(() -> userService.createUser(user2))
                .isInstanceOf(SameEmailException.class);
    }

    @Test
    void updateUser_whenOtherUserHasSameEmail_thenThrowsSameEmailException() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("first@email.com");
        em.persist(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("second@email.com");
        em.persist(user2);
        em.flush();

        User updateData = new User();
        updateData.setEmail("first@email.com");

        assertThatThrownBy(() -> userService.updateUser(user2.getId(), updateData))
                .isInstanceOf(SameEmailException.class);
    }

    @Test
    void deleteUser_shouldRemoveUserFromDatabase() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@email.com");
        em.persist(user);
        em.flush();

        userService.deleteUser(user.getId());

        assertThat(em.find(User.class, user.getId())).isNull();
    }
}