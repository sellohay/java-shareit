package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void getItems_returnItemsWithBookingsAndComments() {
        User owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@email.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@email.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Мел");
        item.setDescription("Ультрамел");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setUser(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(5));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(3));
        em.persist(pastBooking);

        Comment comment = new Comment();
        comment.setText("это реально хайп");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now().minusDays(1));
        em.persist(comment);

        em.flush();
        em.clear();

        List<ItemDetailedDto> result = itemService.getItems(owner.getId());

        assertThat(result).hasSize(1);

        ItemDetailedDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo("Мел");
        assertThat(dto.getLastBookingDate()).isNotNull();
        assertThat(dto.getNearestBookingDate()).isNull();

        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("это реально хайп");
        assertThat(dto.getComments().get(0).getAuthorName()).isEqualTo("Арендатор");
    }

    @Test
    void editItem_whenUserIsNotOwner_thenThrowsValidationException() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner1@email.com");
        em.persist(owner);

        User notOwner = new User();
        notOwner.setName("Not Owner");
        notOwner.setEmail("notowner@email.com");
        em.persist(notOwner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);
        em.flush();

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Hacked Name");

        assertThatThrownBy(() -> itemService.editItem(notOwner.getId(), item.getId(), updateDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void searchItems_whenTextIsEmpty_thenReturnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("");
        assertThat(result).isEmpty();
    }

    @Test
    void createComment_whenUserDidNotBookItem_thenThrowsValidationException() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner2@email.com");
        em.persist(owner);

        User randomUser = new User();
        randomUser.setName("Random User");
        randomUser.setEmail("random@email.com");
        em.persist(randomUser);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);
        em.flush();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Fake comment");

        assertThatThrownBy(() -> itemService.createComment(randomUser.getId(), item.getId(), commentDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void editItem_withPartialData_shouldUpdateOnlyProvidedFields() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_edit@email.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Old Name");
        item.setDescription("Old Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);
        em.flush();

        ItemDto updateNameDto = new ItemDto();
        updateNameDto.setName("New Name");
        updateNameDto.setDescription("");
        itemService.editItem(owner.getId(), item.getId(), updateNameDto);

        Item updatedItem = em.find(Item.class, item.getId());
        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Old Desc");

        ItemDto updateDescDto = new ItemDto();
        updateDescDto.setName("");
        updateDescDto.setDescription("New Desc");
        itemService.editItem(owner.getId(), item.getId(), updateDescDto);

        updatedItem = em.find(Item.class, item.getId());
        assertThat(updatedItem.getName()).isEqualTo("New Name");
        assertThat(updatedItem.getDescription()).isEqualTo("New Desc");

        ItemDto updateAvailableDto = new ItemDto();
        updateAvailableDto.setName("");
        updateAvailableDto.setDescription("");
        updateAvailableDto.setAvailable(false);
        itemService.editItem(owner.getId(), item.getId(), updateAvailableDto);

        updatedItem = em.find(Item.class, item.getId());
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void searchItems_whenTextIsValid_shouldReturnMatchingItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_search@email.com");
        em.persist(owner);

        Item item1 = new Item();
        item1.setName("Item 1111");
        item1.setDescription("Desc 1");
        item1.setAvailable(true);
        item1.setUser(owner);
        em.persist(item1);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Desc 2");
        item2.setAvailable(true);
        item2.setUser(owner);
        em.persist(item2);
        em.flush();

        List<ItemDto> result = itemService.searchItems("1111");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Item 1111");
    }

    @Test
    void getItems_whenUserHasNoItems_thenReturnEmptyList() {
        User user = new User();
        user.setName("No Items User");
        user.setEmail("noitems@email.com");
        em.persist(user);
        em.flush();

        List<ItemDetailedDto> result = itemService.getItems(user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void createItem_withInvalidRequestId_thenThrowsNotFoundException() {
        User user = new User();
        user.setName("Req User");
        user.setEmail("requser@email.com");
        em.persist(user);
        em.flush();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Desc");
        itemDto.setAvailable(true);
        itemDto.setRequestId(999L);

        assertThatThrownBy(() -> itemService.createItem(user.getId(), itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemById_whenUserIsNotOwner_shouldNotReturnBookings() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_viewer@email.com");
        em.persist(owner);

        User viewer = new User();
        viewer.setName("Viewer");
        viewer.setEmail("viewer@email.com");
        em.persist(viewer);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);
        em.flush();

        ItemDetailedDto dto = itemService.getItemById(viewer.getId(), item.getId());

        assertThat(dto.getLastBookingDate()).isNull();
        assertThat(dto.getNearestBookingDate()).isNull();
    }

    @Test
    void checkItemAvailable_whenItemIsNotAvailable_thenThrowsValidationException() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_unavail@email.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(false);
        item.setUser(owner);
        em.persist(item);
        em.flush();

        assertThatThrownBy(() -> itemService.checkItemAvailable(item.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getItemsByRequest_shouldReturnItems() {
        User user = new User();
        user.setName("User");
        user.setEmail("user_req_list@email.com");
        em.persist(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("Need a tool");
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        em.persist(request);

        Item item = new Item();
        item.setName("Tool");
        item.setDescription("Good tool");
        item.setAvailable(true);
        item.setUser(user);
        item.setRequest(request);
        em.persist(item);
        em.flush();

        List<ItemDto> resultSingle = itemService.getItemsByRequest(request.getId());
        assertThat(resultSingle).hasSize(1);
        assertThat(resultSingle.get(0).getName()).isEqualTo("Tool");

        List<ItemDto> resultList = itemService.getItemsByRequests(List.of(request.getId()));
        assertThat(resultList).hasSize(1);
    }

    @Test
    void createItem_withValidRequestId_shouldAssignRequestToItem() {
        User user = new User();
        user.setName("Requester");
        user.setEmail("has_request@email.com");
        em.persist(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("nuzhen mel");
        request.setCreated(LocalDateTime.now());
        request.setUser(user);
        em.persist(request);
        em.flush();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Mel");
        itemDto.setDescription("Good mel");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        ItemDto savedItem = itemService.createItem(user.getId(), itemDto);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getRequestId()).isEqualTo(request.getId());
    }
}