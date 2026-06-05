package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
public class BookingCreateDto {
    private Long itemId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long userId;
}
