package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    ItemRequest save(ItemRequest request);

    List<ItemRequest> findAllByUserIdNot(Long userId);

    boolean existsById(Long id);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAllByUserIdOrderByCreatedDesc(Long userId);
}
