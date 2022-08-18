package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.user.DB.UserDB;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDB userDB;
    private long counterId = 0;

    @Override
    public List<UserDto> getAll() {
        return userDB.getAll();
    }

    @Override
    public UserDto get(long userId) throws NotFoundParameterException {
        return userDB.get(userId);
    }

    @Override
    public UserDto add(UserDto userDto) throws CreatingException {
        checkEmail(userDto);
        userDto.setId(++counterId);
        return userDB.add(userDto);
    }

    @Override
    public void delete(long userId) {
        userDB.delete(userId);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) throws CreatingException, NotFoundParameterException {
        UserDto oldUserDto = get(userId);
        oldUserDto.setId(userId);
        if (userDto.getEmail() != null) {
            checkEmail(userDto);
            oldUserDto.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null)
            oldUserDto.setName(userDto.getName());
        return userDB.add(oldUserDto);
    }

    private void checkEmail(UserDto userDto) throws CreatingException {
        if ((userDto.getEmail() == null) || (!userDto.getEmail().contains("@"))) {
            throw new CreatingException("bad email");
        }
        if ((getAll().size() > 0) && getAll().stream()
                .anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new RuntimeException("bad email");
        }
    }
}
