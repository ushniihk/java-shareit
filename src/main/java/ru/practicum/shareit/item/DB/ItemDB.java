package ru.practicum.shareit.item.DB;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDB {
    ItemDto addNew(ItemDto itemDto);

    List<ItemDto> getAllByUser(long userId);

    List<ItemDto> getAll();

    ItemDto get(long itemId);

    void delete(long userId, long itemId);

}
