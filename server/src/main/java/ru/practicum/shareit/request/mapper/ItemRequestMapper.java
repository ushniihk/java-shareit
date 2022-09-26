package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoForResponse;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ItemRequestMapper {

    private final ItemRepository itemRepository;

    public ItemRequestDtoForResponse toItemRequestDtoForResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoForResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                itemRepository.findAllByRequest(itemRequest.getId()).stream()
                        .map(ItemMapper::toItemDtoWithBooking).collect(Collectors.toList())
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor()
        );
    }
}
