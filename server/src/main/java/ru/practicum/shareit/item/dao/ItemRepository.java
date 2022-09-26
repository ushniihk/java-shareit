package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerOrderById(Long userId, PageRequest pageRequest);

    @Query(value = "select i from Item i")
    Page<Item> findAll(PageRequest pageRequest);

    List<Item> findAllByOwnerOrderById(Long userId);

    List<Item> findAllByRequest(long requestId);


}
