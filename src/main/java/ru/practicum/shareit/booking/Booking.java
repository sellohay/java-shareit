package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDate;

@Data
public class Booking {
    private Long id;
    private Long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private Long bookerId;
}
