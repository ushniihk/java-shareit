package ru.practicum.shareit.user.DB;

import ru.practicum.shareit.exceptions.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDB {

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto get(Long userId) throws NotFoundParameterException;

    void delete(Long userId);

}
