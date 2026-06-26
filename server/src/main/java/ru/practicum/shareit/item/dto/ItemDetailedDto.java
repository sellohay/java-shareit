package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemDetailedDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("lastBooking")
    private LocalDateTime lastBookingDate;
    @JsonProperty("nextBooking")
    private LocalDateTime nearestBookingDate;

    List<CommentDto> comments;
}
