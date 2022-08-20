package ru.practicum.shareit.item.dao;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Data
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Map<Long, Item>> userItemIndex = new LinkedHashMap<>();
    private final Map<Long, Item> items = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public ItemDto addNew(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userStorage.get(userId)));
        userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new HashMap<>());
        userItemIndex.get(userId).put(itemDto.getId(), ItemMapper.toItem(itemDto));
        items.put(itemDto.getId(), ItemMapper.toItem(itemDto));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByUser(long userId) {
        if (!userItemIndex.containsKey(userId))
            throw new NotFoundParameterException("bad user id");
        return userItemIndex.get(userId).values().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAll() {
        return items.values().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public void delete(long userId, long itemId) {
        userItemIndex.get(userId).remove(itemId);
        items.remove(itemId);
    }

}
