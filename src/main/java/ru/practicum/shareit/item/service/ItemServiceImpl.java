package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDetailedDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingStorage bookingStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final CommentStorage commentStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, BookingStorage bookingStorage,
                           ItemRequestStorage itemRequestStorage, CommentStorage commentStorage) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.bookingStorage = bookingStorage;
        this.itemRequestStorage = itemRequestStorage;
        this.commentStorage = commentStorage;
    }

    @Override
    public List<ItemDetailedDto> getItems(Long id) {
        userService.checkUserExists(id);

        List<Item> items = itemStorage.findByUserId(id);
        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> bookings = bookingStorage.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Long, List<Comment>> commentsByItem = commentStorage.findAllByItemIdIn(itemIds)
                .stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> processItem(item, bookingsByItem, commentsByItem, now))
                .toList();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userService.checkUserExists(userId);
        if (itemDto.getRequestId() != null && !itemRequestStorage.existsById(itemDto.getRequestId())) {
            throw new NotFoundException("Неверно указан запрос (id=" + itemDto.getRequestId() + ")");
        }
        validateItemDto(itemDto);
        Item item = ItemMapper.mapToItem(itemDto);
        item.setUser(userService.getUserById(userId));
        item.setRequest(null);
        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> reqOpt = itemRequestStorage.findById(itemDto.getRequestId());
            item.setRequest(reqOpt.orElse(null));
        }
        itemStorage.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        userService.checkUserExists(userId);
        checkItemExists(itemId);
        checkItemOwner(itemId, userId);
        Item item = ItemMapper.mapToItem(itemDto);
        Item oldItem = itemStorage.findById(itemId).get();
        if (!item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (!item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        itemStorage.save(oldItem);
        return ItemMapper.mapToItemDto(oldItem);
    }

    @Override
    public ItemDetailedDto getItemById(Long userId, Long itemId) {
        checkItemExists(itemId);
        Item item = getItemEntityById(itemId);

        List<CommentDto> comments = commentStorage.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        if (item.getUser().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingStorage.findByItemIdInAndStatus(List.of(itemId), BookingStatus.APPROVED);

            lastBooking = bookings.stream()
                    .map(Booking::getStartDate)
                    .filter(b -> b.isBefore(now))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            nextBooking = bookings.stream()
                    .map(Booking::getStartDate)
                    .filter(b -> b.isAfter(now))
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
        }

        return ItemMapper.mapToItemWithDatesDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemsFound = itemStorage.search(text);
        return itemsFound.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void checkItemExists(Long itemId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException("Вещи с id=" + itemId + "не найдено");
        }
    }

    @Override
    public void checkItemOwner(Long itemId, Long userId) {
        if (!itemStorage.existsByIdAndUserId(itemId, userId)) {
            throw new ValidationException("Пользователь id=" + userId + " не является владельцем вещи id=" + itemId);
        }
    }

    @Override
    public boolean checkUserHasItems(Long userId) {
        return itemStorage.existsByUserId(userId);
    }

    @Override
    public void checkItemAvailable(Long itemId) {
        Item item = itemStorage.findById(itemId).get();
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать недоступную вещь");
        }
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto dto) {
        userService.checkUserExists(userId);
        checkItemExists(itemId);

        if (!bookingStorage.existsByUserIdAndItemIdAndStatusAndEndDateBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ValidationException("Пользователь id=" + userId + " не брал данную вещь в аренду");
        }

        Comment comment = CommentMapper.mapToComment(dto);
        comment.setItem(itemStorage.findById(itemId).get());
        comment.setAuthor(userService.getUserById(userId));
        comment = commentStorage.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<Long> requestIds) {
        return itemStorage.findAllByRequestIdIn(requestIds)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItemsByRequest(Long requestId) {
        return itemStorage.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public Item getItemEntityById(Long itemId) {
        checkItemExists(itemId);
        return itemStorage.findById(itemId).get();
    }


    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Отсутствует имя вещи");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Отсутствует описание вещи");
        }
    }

    private ItemDetailedDto processItem(Item item, Map<Long, List<Booking>> bookingsByItem,
                                        Map<Long, List<Comment>> commentsByItem, LocalDateTime now) {
        List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), new ArrayList<>());

        LocalDateTime lastBookingDate = itemBookings.stream()
                .map(Booking::getStartDate)
                .filter(date -> date.isBefore(now))
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime nearestBookingDate = itemBookings.stream()
                .map(Booking::getStartDate)
                .filter(date -> date.isAfter(now))
                .min(LocalDateTime::compareTo)
                .orElse(null);

        List<CommentDto> comments = commentsByItem.getOrDefault(item.getId(), new ArrayList<>())
                .stream().map(CommentMapper::mapToCommentDto).toList();

        return ItemMapper.mapToItemWithDatesDto(item, lastBookingDate, nearestBookingDate, comments);
    }
}
