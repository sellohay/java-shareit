package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(NewItemRequest req, Long userId);

    List<ItemRequestDto> getAllRequestsByOthers(Long userId);

    List<ItemRequestDtoWithResponses> getAllRequests(Long userId);

    ItemRequestDtoWithResponses getRequestById(Long userId, Long requestId);
}
