package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingCreateDto {
    @NotNull
    private Long itemId;
    @NotNull
    @JsonProperty("start")
    private LocalDateTime startDate;
    @NotNull
    @JsonProperty("end")
    private LocalDateTime endDate;
}
