package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select * from items where owner_id = ?1 ORDER BY id " +
            "offset ?2", nativeQuery = true)
    Page<Item> findAllByOwnerOrderById(Long userId, int from, PageRequest pageRequest);

    @Query(value = "select * from items ORDER BY id " +
            "offset ?1", nativeQuery = true)
    Page<Item> findAll(int from, PageRequest pageRequest);

    List<Item> findAllByOwnerOrderById(Long userId);

    List<Item> findAllByRequest(long requestId);


}
