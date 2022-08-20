package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addNew(long userId, ItemDto itemDto);

    List<ItemDto> getAllByUser(long userId);

    List<ItemDto> getAll();


    ItemDto get(long userId, long itemId);

    void delete(long userId, long itemId);

}
