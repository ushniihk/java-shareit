package ru.practicum.shareit.user.DB;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserDbImpl implements UserDB {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<UserDto> getAll() {
        return users.values().stream().map(User::toUserDto).collect(Collectors.toList());
    }

    public UserDto add(UserDto userDto) {
        users.put(userDto.getId(), UserDto.toUser(userDto));
        return userDto;
    }

    public UserDto get(Long userId) throws NotFoundParameterException {
        if (!users.containsKey(userId))
            throw new NotFoundParameterException("bad user ID");
        return User.toUserDto(users.get(userId));
    }

    public void delete(Long userId) {
        users.remove(userId);
    }

}
