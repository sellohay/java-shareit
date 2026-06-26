package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

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
}