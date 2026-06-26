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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    void approveBooking_whenUserIsNotOwner_thenThrowsValidationException() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner1@email.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker1@email.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setUser(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(2));
        em.persist(booking);
        em.flush();

        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), booking.getId(), true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getBooking_whenUserIsNeitherBookerNorOwner_thenThrowsValidationException() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner2@email.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker2@email.com");
        em.persist(booker);

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

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setUser(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(2));
        em.persist(booking);
        em.flush();

        assertThatThrownBy(() -> bookingService.getBooking(randomUser.getId(), booking.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getBookingsForUser_whenStateIsInvalid_thenThrowsValidationException() {
        User user = new User();
        user.setName("User");
        user.setEmail("user1@email.com");
        em.persist(user);
        em.flush();

        assertThatThrownBy(() -> bookingService.getBookingsForUser(user.getId(), "INVALID_STATE"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getAllBookingStates_shouldHitAllSwitchBranches() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_states@email.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker_states@email.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Item States");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setUser(owner);
        em.persist(item);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setUser(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setStartDate(LocalDateTime.now().minusDays(1));
        currentBooking.setEndDate(LocalDateTime.now().plusDays(1));
        em.persist(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setUser(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStartDate(LocalDateTime.now().minusDays(5));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(3));
        em.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setUser(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        futureBooking.setStartDate(LocalDateTime.now().plusDays(3));
        futureBooking.setEndDate(LocalDateTime.now().plusDays(5));
        em.persist(futureBooking);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item);
        rejectedBooking.setUser(booker);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking.setStartDate(LocalDateTime.now().plusDays(10));
        rejectedBooking.setEndDate(LocalDateTime.now().plusDays(12));
        em.persist(rejectedBooking);

        em.flush();

        assertThat(bookingService.getBookingsForUser(booker.getId(), "ALL")).hasSize(4);
        assertThat(bookingService.getBookingsForUser(booker.getId(), "CURRENT")).hasSize(1);
        assertThat(bookingService.getBookingsForUser(booker.getId(), "PAST")).hasSize(1);
        assertThat(bookingService.getBookingsForUser(booker.getId(), "FUTURE")).hasSize(2);
        assertThat(bookingService.getBookingsForUser(booker.getId(), "WAITING")).hasSize(1);
        assertThat(bookingService.getBookingsForUser(booker.getId(), "REJECTED")).hasSize(1);

        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "ALL")).hasSize(4);
        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "CURRENT")).hasSize(1);
        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "PAST")).hasSize(1);
        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "FUTURE")).hasSize(2);
        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "WAITING")).hasSize(1);
        assertThat(bookingService.getItemBookingsOfUser(owner.getId(), "REJECTED")).hasSize(1);
    }
}