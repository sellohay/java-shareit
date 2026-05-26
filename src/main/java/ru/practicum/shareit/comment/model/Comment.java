package ru.practicum.shareit.comment.model;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long itemId;
    private String text;
}
