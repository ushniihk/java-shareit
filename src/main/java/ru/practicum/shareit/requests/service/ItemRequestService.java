package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.ItemRequestDtoForResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoForResponse add(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoForResponse> getAllByUser(long userId);

    ItemRequestDtoForResponse get(long userId, long requestId);

    List<ItemRequestDtoForResponse> getAllByOtherUsers(long userId, int from, int size);
}
