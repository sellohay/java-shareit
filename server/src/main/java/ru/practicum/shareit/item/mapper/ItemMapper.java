package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        itemDto.setOwnerId(item.getUser().getId());
        return itemDto;
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : "");
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : "");
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDetailedDto mapToItemWithDatesDto(Item item,
                                                        LocalDateTime lastBookingDate, LocalDateTime nearestBookingDate,
                                                        List<CommentDto> comments) {
        ItemDetailedDto dto = new ItemDetailedDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBookingDate(lastBookingDate);
        dto.setNearestBookingDate(nearestBookingDate);
        dto.setComments(comments);
        return dto;
    }

    public static ItemResponseDto mapToItemResponseDto(ItemDto item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setItemId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setOwnerId(item.getOwnerId());
        return itemResponseDto;
    }
}
