package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingDto {
    private Long id;
    private ItemDto item;
    @JsonProperty("start")
    private LocalDateTime startDate;
    @JsonProperty("end")
    private LocalDateTime endDate;
    private BookingStatus status;
    private UserDto booker;
}
