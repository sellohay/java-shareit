package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}