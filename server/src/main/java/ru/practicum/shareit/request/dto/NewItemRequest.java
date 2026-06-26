package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewItemRequest {
    private String description;
    private LocalDateTime created;
}
