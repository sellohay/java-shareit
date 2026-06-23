package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDetailedDto> getItems(Long id);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDetailedDto getItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);

    void checkItemExists(Long itemId);

    Item getItemEntityById(Long itemId);

    void checkItemOwner(Long itemId, Long userId);

    boolean checkUserHasItems(Long userId);

    void checkItemAvailable(Long itemId);

    CommentDto createComment(Long userId, Long itemId, CommentDto dto);

    List<ItemDto> getItemsByRequests(List<Long> requestIds);

    List<ItemDto> getItemsByRequest(Long requestId);
}
