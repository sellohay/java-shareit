package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto dto);

    BookingDto approveBooking(Long userId, Long bookingId, boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsForUser(Long userId, String state);

    List<BookingDto> getItemBookingsOfUser(Long userId, String state);
}
