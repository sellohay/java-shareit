package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findByUserId(Long userId);

    Item save(Item item);

    boolean existsById(Long id);

    Optional<Item> findById(Long id);

    @Query("""
        SELECT i FROM Item i
        WHERE i.available = true
        AND (
            LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))
        )
    """)
    List<Item> search(String text);

    boolean existsByIdAndUserId(Long id, Long userId);

    boolean existsByUserId(Long userId);
}
