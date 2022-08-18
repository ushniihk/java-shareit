package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.DB.ItemDB;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.DB.UserDB;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDB itemDB;
    private final UserDB userDB;
    private long idCounter = 0;


    @Override
    public List<ItemDto> getAllByUser(long userId) {
        return itemDB.getAllByUser(userId);
    }

    @Override
    public ItemDto get(long userId, long itemId) {
        return itemDB.get(itemId);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemDB.getAll().stream()
                .filter(itemDto -> itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(itemDto -> itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNew(long userId, ItemDto itemDto) throws NotFoundParameterException, CreatingException {
        checkItem(itemDto);
        itemDto.setOwner(UserDto.toUser(userDB.get(userId)));
        itemDto.setId(++idCounter);
        return itemDB.addNew(itemDto);
    }

    @Override
    public void delete(long userId, long itemId) {
        itemDB.delete(userId, itemId);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) throws UpdateException {
        ItemDto oldItemDto = get(userId, itemId);
        if (userId != oldItemDto.getOwner().getId()) {
            throw new UpdateException("bad user id");
        }
        oldItemDto.setId(itemId);
        if (itemDto.getName() != null) {
            oldItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItemDto.setAvailable(itemDto.getAvailable());
        }
        return itemDB.addNew(oldItemDto);
    }

    private void checkItem(ItemDto itemDto) throws CreatingException {
        if (itemDto.getAvailable() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null)
            throw new CreatingException("ha-ha, try better, bad item");
    }

}
