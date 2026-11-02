package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentStorage extends JpaRepository<Comment, Long> {
    Comment save(Comment comment);

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}
