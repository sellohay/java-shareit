package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequest;

@Controller
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid NewItemRequest req) {
        return itemRequestClient.createRequest(userId, req);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsByOthers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllRequestsByOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}