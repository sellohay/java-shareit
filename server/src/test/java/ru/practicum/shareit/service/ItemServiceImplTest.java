package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}