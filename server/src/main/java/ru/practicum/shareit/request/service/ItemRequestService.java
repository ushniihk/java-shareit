package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoForResponse add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoForResponse> getAllByUser(long userId);

    ItemRequestDtoForResponse get(long userId, long requestId);

    List<ItemRequestDtoForResponse> getAllByOtherUsers(long userId, int from, int size);
}
