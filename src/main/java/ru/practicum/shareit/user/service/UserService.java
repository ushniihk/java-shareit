package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto get(long userId) throws NotFoundParameterException;

    UserDto add(UserDto userDto) throws CreatingException;

    void delete(long userId);

    UserDto update(long userId, UserDto userDto) throws CreatingException, NotFoundParameterException;

}
