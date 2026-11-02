package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUsers_shouldReturnListOfUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Тимон");

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Пумба");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        User user = new User();
        user.setName("Тимон");
        user.setEmail("timon@yandex.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Тимон");
        savedUser.setEmail("timon@yandex.ru");

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        User updateData = new User();
        updateData.setName("Старый Пумба");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Новый Пумба");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateData))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Новый Пумба"));
    }

    @Test
    void deleteUser_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}