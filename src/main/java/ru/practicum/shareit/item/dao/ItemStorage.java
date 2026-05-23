package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItemsForUser(Long id);

    Item createItem(long userId, Item itemDto);

    boolean checkItemExists(Long itemId);

    Item getItemById(Long itemId);

    List<Item> searchItems(String text);

    Item editItem(Long itemId, Item item);

    boolean isOwner(Long itemId, Long userId);
}
