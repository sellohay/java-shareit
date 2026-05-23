package ru.practicum.shareit.request;

import lombok.Data;

@Data
public class ItemRequest {
    private Long id;
    private Long userId;
    private String name;
}
