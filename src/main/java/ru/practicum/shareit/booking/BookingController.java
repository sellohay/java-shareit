package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingCreateDto dto) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingCreateDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody BookingCreateDto dto,
                                           @RequestParam boolean isApproved) {
        return bookingService.approveBooking(userId, dto, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingCreateDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingCreateDto> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(
            required = false,
            defaultValue = "ALL") String state) {
        return bookingService.getBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingCreateDto> getItemBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(
            required = false,
            defaultValue = "ALL") String state) {
        return bookingService.getItemBookingsOfUser(userId, state);
    }


}
