package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final Map<Long, String> emails = new HashMap<>();
    private long counterId = 0;

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll();
    }

    @Override
    public UserDto get(long userId) {
        return userStorage.get(userId);
    }

    @Override
    public UserDto add(UserDto userDto) {
        checkEmail(userDto);
        userDto.setId(++counterId);
        emails.put(userDto.getId(), userDto.getEmail());
        return userStorage.add(userDto);
    }

    @Override
    public void delete(long userId) {
        emails.remove(userId);
        userStorage.delete(userId);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        UserDto oldUserDto = get(userId);
        oldUserDto.setId(userId);
        if (userDto.getEmail() != null) {
            checkEmail(userDto);
            oldUserDto.setEmail(userDto.getEmail());
            emails.put(userId, userDto.getEmail());
        }
        if (userDto.getName() != null)
            oldUserDto.setName(userDto.getName());
        return userStorage.add(oldUserDto);
    }

    private void checkEmail(UserDto userDto) {
        if ((userDto.getEmail() == null) || (!userDto.getEmail().contains("@"))) {
            throw new CreatingException("bad email");
        }
        if ((emails.size() > 0) && emails.containsValue(userDto.getEmail())) {
            throw new RuntimeException("bad email");
        }
    }
}
