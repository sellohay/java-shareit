package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Booking save(Booking booking);

    Booking findById(long id);

    List<Booking> findAllByUserId(long userId, Sort sort);

    List<Booking> findAllByUserIdAndEndDateBefore(long userId, LocalDateTime endDate, Sort sort);

    List<Booking> findAllByUserIdAndStartDateAfter(long userId, LocalDateTime startDate, Sort sort);

    List<Booking> findAllByUserIdAndStatus(long userId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.user.id = ?1 AND b.startDate <= ?2 AND b.endDate >= ?2")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemUserId(Long ownerId, Sort sort);

    List<Booking> findAllByItemUserIdAndEndDateBefore(Long ownerId, LocalDateTime endDate, Sort sort);

    List<Booking> findAllByItemUserIdAndStartDateAfter(Long ownerId, LocalDateTime startDate, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.user.id = ?1 AND b.startDate <= ?2 AND b.endDate >= ?2")
    List<Booking> findCurrentBookingsByOwnerId(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemUserIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus status);

    boolean existsByUserIdAndItemIdAndStatusAndEndDateBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime now);
}
