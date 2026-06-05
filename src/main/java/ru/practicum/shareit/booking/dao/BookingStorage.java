package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Booking save(Booking booking);
}
