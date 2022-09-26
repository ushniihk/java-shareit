package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoForResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoForResponse add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoForResponse> getAllByUser(long userId);

    ItemRequestDtoForResponse get(long userId, long requestId);

    List<ItemRequestDtoForResponse> getAllByOtherUsers(long userId, int from, int size);
}
