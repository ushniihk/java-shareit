package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto get(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundParameterException("bad id");
        }
        return UserMapper.toUserDto(userStorage.getReferenceById(userId));
    }

    @Override
    public UserDto add(UserDto userDto) {
        checkEmail(userDto);
        userStorage.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(userStorage.getUserByEmail(userDto.getEmail()));
    }

    @Override
    public void delete(long userId) {
        userStorage.deleteAllById(List.of(userId));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        UserDto oldUserDto = get(userId);
        oldUserDto.setId(userId);
        if (userDto.getEmail() != null) {
            checkEmail(userDto);
            oldUserDto.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null)
            oldUserDto.setName(userDto.getName());
        userStorage.save(UserMapper.toUser(oldUserDto));
        return oldUserDto;
    }

    private void checkEmail(UserDto userDto) {
        if ((userDto.getEmail() == null) || (!userDto.getEmail().contains("@"))) {
            throw new CreatingException("bad email");
        }
    }
}
