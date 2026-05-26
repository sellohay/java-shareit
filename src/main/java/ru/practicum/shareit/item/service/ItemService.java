package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long id);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> searchItems(String text);

    void validateUser(Long userId);

    boolean checkItemExists(Long itemId);

    void checkItemOwner(Long itemId, Long userId);
}
