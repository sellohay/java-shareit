package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long userId;

    private List<Comment> comments;
}
