package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody @Valid NewItemRequest req) {
        return itemRequestService.createRequest(req, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithResponses> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsByOthers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsByOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithResponses getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable("requestId") Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }


}
