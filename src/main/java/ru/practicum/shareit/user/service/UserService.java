package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto get(long userId);

    UserDto add(UserDto userDto);

    void delete(long userId);

    UserDto update(long userId, UserDto userDto);

}
