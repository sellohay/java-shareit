package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoWithResponses {
    private Long id;
    private String description;
    private LocalDateTime created;
    @JsonProperty("items")
    private List<ItemResponseDto> responses;
}
