package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> getItems(Long id) {
        validateUser(id);
        return itemStorage.getItemsForUser(id)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        validateUser(userId);
        validateItemDto(itemDto);
        Item createdItem = itemStorage.createItem(userId, ItemMapper.mapToItem(itemDto));
        return ItemMapper.mapToItemDto(createdItem);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        validateUser(userId);
        checkItemExists(itemId);
        checkItemOwner(itemId, userId);
        Item editedItem = itemStorage.editItem(itemId, ItemMapper.mapToItem(itemDto));
        return ItemMapper.mapToItemDto(editedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (!checkItemExists(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        }
        return ItemMapper.mapToItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsFound = itemStorage.searchItems(text);
        return itemsFound.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void validateUser(Long userId) {
        if (!userService.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    @Override
    public boolean checkItemExists(Long itemId) {
        return itemStorage.checkItemExists(itemId);
    }

    @Override
    public void checkItemOwner(Long itemId, Long userId) {
        if (!itemStorage.isOwner(itemId, userId)) {
            throw new ValidationException("Пользователь id=" + userId + " не является владельцем вещи id=" + itemId);
        }
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Отсутствует имя вещи");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Отсутствует описание вещи");
        }
    }
}
