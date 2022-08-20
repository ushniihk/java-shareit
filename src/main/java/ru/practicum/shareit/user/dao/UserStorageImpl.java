package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<UserDto> getAll() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return userDto;
    }

    public UserDto get(Long userId) {
        if (!users.containsKey(userId))
            throw new NotFoundParameterException("bad user ID");
        return UserMapper.toUserDto(users.get(userId));
    }

    public void delete(Long userId) {
        users.remove(userId);
    }

}
