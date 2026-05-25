package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemInMemoryStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idCount = 0L;

    @Override
    public List<Item> getItemsForUser(Long id) {
        return items.values().stream()
                .filter(item -> item.getUserId().equals(id))
                .toList();
    }

    @Override
    public Item createItem(long userId, Item itemDto) {
        idCount++;
        Item item = new Item();
        item.setId(idCount);
        item.setUserId(userId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setComments(new ArrayList<>());
        items.put(idCount, item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values()
                .stream()
                .filter(item -> ((item.getName() != null)
                        && (item.getName().toLowerCase().contains(text.toLowerCase())))
                        || ((item.getDescription() != null)
                        && item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(item -> item.getAvailable().equals(true))
                .toList();
    }

    @Override
    public Item editItem(Long itemId, Item item) {
        Item oldItem = items.get(itemId);
        if (!item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (!item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, oldItem);
        return oldItem;
    }

    @Override
    public boolean isOwner(Long itemId, Long userId) {
        Item item = items.get(itemId);
        return item.getUserId().equals(userId);
    }

    @Override
    public boolean checkItemExists(Long itemId) {
        return items.containsKey(itemId);
    }
}
