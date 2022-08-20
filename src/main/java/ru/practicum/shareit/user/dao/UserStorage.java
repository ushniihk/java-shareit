package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto get(Long userId);

    void delete(Long userId);

}
