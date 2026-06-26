package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void createBooking_shouldSaveAndReturnBookingDto() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        em.persist(owner);

        Item item = new Item();
        item.setName("Mel");
        item.setDescription("Nuzhen mel");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        em.persist(booker);

        em.flush();

        BookingCreateDto inputDto = new BookingCreateDto();
        inputDto.setItemId(item.getId());
        inputDto.setStartDate(LocalDateTime.now().plusDays(1));
        inputDto.setEndDate(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createBooking(booker.getId(), inputDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking savedBooking = em.find(Booking.class, result.getId());
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}