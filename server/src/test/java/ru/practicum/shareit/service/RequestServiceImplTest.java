package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    void getRequestById_whenRequestNotFound_thenThrowsNotFoundException() {
        User user = new User();
        user.setName("User");
        user.setEmail("user1@email.com");
        em.persist(user);
        em.flush();

        assertThatThrownBy(() -> itemRequestService.getRequestById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAllRequestsByOthers_shouldReturnRequestsFromOtherUsers() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("u1@email.com");
        em.persist(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("u2@email.com");
        em.persist(user2);

        ItemRequest requestByUser1 = new ItemRequest();
        requestByUser1.setDescription("Request 1");
        requestByUser1.setCreated(LocalDateTime.now());
        requestByUser1.setUser(user1);
        em.persist(requestByUser1);
        em.flush();

        List<ItemRequestDto> requests = itemRequestService.getAllRequestsByOthers(user2.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Request 1");
    }

    @Test
    void createRequest_shouldSaveAndReturnDto() {
        User user = new User();
        user.setName("Creator");
        user.setEmail("creator@email.com");
        em.persist(user);
        em.flush();

        NewItemRequest newReq = new NewItemRequest();
        newReq.setDescription("important zapros");

        ItemRequestDto result = itemRequestService.createRequest(newReq, user.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("important zapros");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void getAllRequests_whenNoRequests_thenReturnEmptyList() {
        User user = new User();
        user.setName("Empty User");
        user.setEmail("empty_req@email.com");
        em.persist(user);
        em.flush();

        List<ItemRequestDtoWithResponses> result = itemRequestService.getAllRequests(user.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllRequests_whenRequestHasNoItems_thenReturnWithEmptyResponses() {
        User user = new User();
        user.setName("Requester");
        user.setEmail("requester@email.com");
        em.persist(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("desc 1");
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        em.persist(request);
        em.flush();

        List<ItemRequestDtoWithResponses> result = itemRequestService.getAllRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("desc 1");
        assertThat(result.get(0).getResponses()).isEmpty();
    }

    @Test
    void getRequestById_whenRequestFound_thenReturnRequestWithResponses() {
        User requester = new User();
        requester.setName("Requester");
        requester.setEmail("req_found@email.com");
        em.persist(requester);

        ItemRequest request = new ItemRequest();
        request.setDescription("Found me");
        request.setCreated(LocalDateTime.now());
        request.setUser(requester);
        em.persist(request);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_found@email.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Item for request");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        item.setRequest(request);
        em.persist(item);
        em.flush();

        ItemRequestDtoWithResponses result = itemRequestService.getRequestById(requester.getId(), request.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo("Found me");
        assertThat(result.getResponses()).hasSize(1);
        assertThat(result.getResponses().get(0).getName()).isEqualTo("Item for request");
    }
}