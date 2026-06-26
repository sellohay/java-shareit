package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void getAllRequests_shouldReturnRequestsWithItems() {
        User requester = new User();
        requester.setName("User");
        requester.setEmail("req@email.com");
        em.persist(requester);

        ItemRequest request = new ItemRequest();
        request.setDescription("НУЖЕН МЕЛ");
        request.setCreated(LocalDateTime.now());
        request.setUser(requester);
        em.persist(request);

        User itemOwner = new User();
        itemOwner.setName("Owner");
        itemOwner.setEmail("own@email.com");
        em.persist(itemOwner);

        Item item = new Item();
        item.setName("Мел");
        item.setDescription("Ультрамел");
        item.setAvailable(true);
        item.setUser(itemOwner);
        item.setRequest(request);
        em.persist(item);

        em.flush();

        List<ItemRequestDtoWithResponses> result = itemRequestService.getAllRequests(requester.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("НУЖЕН МЕЛ");
        assertThat(result.get(0).getResponses()).hasSize(1);
        assertThat(result.get(0).getResponses().get(0).getName()).isEqualTo("Мел");
        assertThat(result.get(0).getResponses().get(0).getItemId()).isEqualTo(item.getId());
    }
}