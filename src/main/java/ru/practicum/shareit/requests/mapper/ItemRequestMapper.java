package ru.practicum.shareit.requests.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.ItemRequestDtoForResponse;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemRequestDtoForResponse toItemRequestDtoForResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoForResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                itemRepository.findAllByRequest(itemRequest.getId()).stream()
                        .map(itemMapper::toItemDtoWithBooking).collect(Collectors.toList())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor()
        );
    }
}
