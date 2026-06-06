package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingStorage bookingStorage, UserService userService, ItemService itemService) {
        this.bookingStorage = bookingStorage;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto createBooking(Long userId, BookingCreateDto dto) {
        userService.checkUserExists(userId);
        itemService.checkItemExists(dto.getItemId());
        itemService.checkItemAvailable(dto.getItemId());
        Booking booking = BookingMapper.mapToBooking(dto);
        booking.setUser(userService.getUserById(userId));
        booking.setItem(itemService.getItemEntityById(dto.getItemId()));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingStorage.save(booking);
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean isApproved) {
        Booking booking = checkBookingExists(bookingId);
        Item item = booking.getItem();
        if (!Objects.equals(item.getUser().getId(), userId)) {
            throw new ValidationException("Пользователь id=" + userId + " не является владельцем вещи");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        bookingStorage.save(booking);
        return BookingMapper.mapToDto(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        userService.checkUserExists(userId);
        Booking booking = checkBookingExists(bookingId);

        Item item = booking.getItem();
        if (!(Objects.equals(item.getUser().getId(), userId) || Objects.equals(booking.getUser().getId(), userId))) {
            throw new ValidationException("Пользователь id=" + userId + " не является владельцем вещи или брони");
        }

        return BookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsForUser(Long userId, String state) {
        userService.checkUserExists(userId);
        if (!State.isValidState(state)) {
            throw new ValidationException("Неверное состояние брони - " + state);
        }
        State bookingState = State.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingStorage.findAllByUserId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingStorage.findCurrentBookings(userId, now, sort);
                break;
            case PAST:
                bookings = bookingStorage.findAllByUserIdAndEndDateBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByUserIdAndStartDateAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByUserIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByUserIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = new ArrayList<>();
                break;
        }
        return bookings.stream()
                .map(BookingMapper::mapToDto)
                .toList();
    }

    @Override
    public List<BookingDto> getItemBookingsOfUser(Long userId, String state) {
        userService.checkUserExists(userId);
        if (!State.isValidState(state)) {
            throw new ValidationException("Неверное состояние брони - " + state);
        }
        if (!itemService.checkUserHasItems(userId)) {
            return new ArrayList<>();
        }

        State bookingState = State.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");

        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingStorage.findAllByItemUserId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingStorage.findCurrentBookingsByOwnerId(userId, now, sort);
                break;
            case PAST:
                bookings = bookingStorage.findAllByItemUserIdAndEndDateBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByItemUserIdAndStartDateAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByItemUserIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByItemUserIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                bookings = new ArrayList<>();
                break;
        }
        return bookings.stream()
                .map(BookingMapper::mapToDto)
                .toList();
    }

    private Booking checkBookingExists(Long bookingId) {
        Optional<Booking> booking = bookingStorage.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Брони с id=" + bookingId + " не найдено");
        }
        return booking.get();
    }
}
