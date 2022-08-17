package ru.practicum.shareit.item.DB;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDbImpl implements ItemDB {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto addNew(ItemDto itemDto) {
        items.put(itemDto.getId(), ItemDto.toItem(itemDto));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByUser(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId)
                .map(Item::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAll() {
        return items.values().stream().map(Item::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long itemId) {
        return Item.toItemDto(items.get(itemId));
    }

    @Override
    public void delete(long userId, long itemId) {
        items.remove(itemId);
    }

}
