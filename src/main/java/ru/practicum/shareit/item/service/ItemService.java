package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllByUser(long userId);

    ItemDto get(long userId, long itemId);

    List<ItemDto> search(long userId, String text);

    ItemDto addNew(long userId, ItemDto itemDto);

    void delete(long userId, long itemId);

    ItemDto update(long userId, long itemId, ItemDto itemDto);


}