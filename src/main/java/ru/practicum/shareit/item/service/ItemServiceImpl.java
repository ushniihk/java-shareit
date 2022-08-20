package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private long idCounter = 0;


    @Override
    public List<ItemDto> getAllByUser(long userId) {
        return itemStorage.getAllByUser(userId);
    }

    @Override
    public ItemDto get(long userId, long itemId) {

        return itemStorage.get(userId, itemId);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemStorage.getAll().stream()
                .filter(itemDto -> itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(itemDto -> itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNew(long userId, ItemDto itemDto) {
        checkItem(itemDto);
        if (!userStorage.getAll().contains(userStorage.get(userId)))
            throw new CreatingException("bad user id");
        itemDto.setId(++idCounter);
        return itemStorage.addNew(userId, itemDto);

    }

    @Override
    public void delete(long userId, long itemId) {
        itemStorage.delete(userId, itemId);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (getAllByUser(userId).stream().anyMatch(itemDto1 -> itemDto1.getId() != itemId)) {
            throw new UpdateException("bad user id");
        }
        ItemDto oldItemDto = get(userId, itemId);
        if (itemDto.getName() != null) {
            oldItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItemDto.setAvailable(itemDto.getAvailable());
        }
        return itemStorage.addNew(userId, oldItemDto);
    }

    private void checkItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null)
            throw new CreatingException("ha-ha, try better, bad item");
    }

}
