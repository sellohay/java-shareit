package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Service
public class BookingServiceImpl implements BookingService{

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
        Booking booking = BookingMapper.mapToBooking(dto);
        booking.setUser(userService.getUserById(userId));
        booking.setItem(ItemMapper.mapToItem(itemService.getItemById(dto.getItemId())));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingStorage.save(booking);
        return BookingMapper.mapToDto(booking);
    }
}
