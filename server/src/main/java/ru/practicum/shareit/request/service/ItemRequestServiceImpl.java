package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDtoForResponse add(long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId))
            throw new NotFoundParameterException("bad user Id");
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty())
            throw new IncorrectParameterException("empty description");
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userId);
        return itemRequestMapper.toItemRequestDtoForResponse(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoForResponse> getAllByUser(long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundParameterException("bad user Id");
        return itemRequestRepository.findAllByRequestor(userId).stream()
                .map(itemRequestMapper::toItemRequestDtoForResponse).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoForResponse get(long userId, long requestId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundParameterException("bad user Id");
        if (!itemRequestRepository.existsById(requestId))
            throw new NotFoundParameterException("bad request id");
        return itemRequestMapper.toItemRequestDtoForResponse(itemRequestRepository.findById(requestId)
                .orElseThrow());
    }

    @Override
    public List<ItemRequestDtoForResponse> getAllByOtherUsers(long userId, int from, int size) {
        if (!userRepository.existsById(userId))
            throw new NotFoundParameterException("bad user Id");
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return itemRequestRepository.findAllByRequestorIsNot(userId, pageRequest).stream()
                .map(itemRequestMapper::toItemRequestDtoForResponse).collect(Collectors.toList());
    }
}
