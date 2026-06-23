package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestStorage storage;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestServiceImpl(ItemRequestStorage storage, UserService userService, ItemService itemService) {
        this.storage = storage;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ItemRequestDto createRequest(NewItemRequest req, Long userId) {
        ItemRequest request = ItemRequestMapper.newToRequest(req);
        request.setUser(userService.getUserById(userId));
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.requestToDto(storage.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByOthers(Long userId) {
        userService.checkUserExists(userId);
        return ItemRequestMapper.requestsToDtos(storage.findAllByUserIdNot(userId));
    }

    @Override
    public List<ItemRequestDtoWithResponses> getAllRequests(Long userId) {
        userService.checkUserExists(userId);
        List<ItemRequest> requests = storage.findAllByUserIdOrderByCreatedDesc(userId);
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> requestsIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<ItemDto> items = itemService.getItemsByRequests(requestsIds);
        Map<Long, List<ItemDto>> itemsByRequests = items.stream()
                .collect(groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> getDtoWithResponse(request, itemsByRequests))
                .toList();
    }

    @Override
    public ItemRequestDtoWithResponses getRequestById(Long userId, Long requestId) {
        userService.checkUserExists(userId);
        if (!storage.existsById(requestId)) {
            throw new NotFoundException("Запрос с id=" + requestId + " не найден");
        }
        List<ItemDto> items = itemService.getItemsByRequest(requestId);
        ItemRequest request = storage.findById(requestId).get();

        List<ItemResponseDto> responses = items.stream()
                .map(ItemMapper::mapToItemResponseDto)
                .toList();
        ItemRequestDtoWithResponses dto = ItemRequestMapper.requestToDtoWithResponses(request);
        dto.setResponses(responses);
        return dto;
    }

    private ItemRequestDtoWithResponses getDtoWithResponse(ItemRequest request,
                                                           Map<Long, List<ItemDto>> itemsByRequests) {
        ItemRequestDtoWithResponses dto = ItemRequestMapper.requestToDtoWithResponses(request);
        List<ItemResponseDto> responses = itemsByRequests
                .getOrDefault(request.getId(), Collections.emptyList())
                .stream()
                .map(ItemMapper::mapToItemResponseDto)
                .toList();
        dto.setResponses(responses);
        return dto;
    }

}
