package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllByUser(long userId);

    ItemDto get(long userId, long itemId);

    List<ItemDto> search(long userId, String text);

    ItemDto addNew(long userId, ItemDto itemDto) throws NotFoundParameterException, CreatingException;

    void delete(long userId, long itemId);

    ItemDto update(long userId, long itemId, ItemDto itemDto) throws IncorrectParameterException, UpdateException;


}