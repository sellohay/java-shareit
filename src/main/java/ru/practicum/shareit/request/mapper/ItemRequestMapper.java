package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest newToRequest(NewItemRequest req) {
        ItemRequest request = new ItemRequest();
        request.setDescription(req.getDescription());
        request.setCreated(req.getCreated());
        return request;
    }

    public static ItemRequestDto requestToDto(ItemRequest req) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(req.getId());
        dto.setDescription(req.getDescription());
        dto.setCreated(req.getCreated());
        return dto;
    }

    public static List<ItemRequestDto> requestsToDtos(List<ItemRequest> reqs) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest req : reqs) {
            dtos.add(requestToDto(req));
        }
        return dtos;
    }

    public static ItemRequestDtoWithResponses requestToDtoWithResponses(ItemRequest req) {
        ItemRequestDtoWithResponses dto = new ItemRequestDtoWithResponses();
        dto.setId(req.getId());
        dto.setDescription(req.getDescription());
        dto.setCreated(req.getCreated());
        return dto;
    }

}
