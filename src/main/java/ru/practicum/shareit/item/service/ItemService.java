package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoWithAuthorAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    Page<ItemDtoWithBooking> getAllByUser(long userId, int from, int size);

    ItemDtoWithBooking get(long userId, long itemId);

    List<ItemDto> search(long userId, String text, int from, int size);

    ItemDtoWithBooking addNew(long userId, ItemDto itemDto);

    void delete(long userId, long itemId);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    CommentDtoWithAuthorAndItem addComment(long userId, long itemId, CommentDto commentDto);


}