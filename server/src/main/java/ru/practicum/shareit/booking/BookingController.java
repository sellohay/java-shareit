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
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(
            required = false,
            defaultValue = "ALL") String state) {
        return bookingService.getBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(
            required = false,
            defaultValue = "ALL") String state) {
        return bookingService.getItemBookingsOfUser(userId, state);
    }
}
