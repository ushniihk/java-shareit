package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private UserDto booker;

    @Mock
    private UserRepository userRepository;

    private UserService service;

    private MockitoSession mockitoSession;

    @BeforeEach
    void init() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        service = new UserServiceImpl(userRepository);

        booker = new UserDto(3, "Bob", "bob@mail.ru");
    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldGetAll() {
        List<User> users = List.of(UserMapper.toUser(booker));

        doReturn(users).when(userRepository).findAll();

        List<UserDto> list = service.getAll();

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(booker.getId()));
        MatcherAssert.assertThat(list.get(0).getEmail(), equalTo(booker.getEmail()));
        MatcherAssert.assertThat(list.get(0).getName(), equalTo(booker.getName()));
        MatcherAssert.assertThat(list.size(), equalTo(1));
    }

    @Test
    void shouldGet() {
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(anyLong());

        UserDto userDto = service.get(booker.getId());

        MatcherAssert.assertThat(userDto.getId(), equalTo(booker.getId()));
        MatcherAssert.assertThat(userDto.getEmail(), equalTo(booker.getEmail()));
        MatcherAssert.assertThat(userDto.getName(), equalTo(booker.getName()));
    }

    @Test
    void shouldAdd() {
        doReturn(UserMapper.toUser(booker)).when(userRepository).getUserByEmail(anyString());

        service.add(booker);

        Mockito.verify(userRepository, Mockito.times(1))
                .save(UserMapper.toUser(booker));
    }

    @Test
    void shouldDelete() {
        service.delete(booker.getId());

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteAllById(List.of(booker.getId()));
    }

    @Test
    void shouldUpdate() {
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(UserMapper.toUser(booker)).when(userRepository).getReferenceById(anyLong());

        service.update(booker.getId(), booker);

        Mockito.verify(userRepository, Mockito.times(1))
                .save(UserMapper.toUser(booker));
    }

    @Test
    void shouldTrowCreationException() {
        booker.setEmail(null);
        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ReflectionTestUtils.invokeMethod(service, "checkEmail", booker));

        MatcherAssert.assertThat("bad email", equalTo(exception.getMessage()));
    }

    @Test
    void shouldThrowNotFondParameterException() {
        doReturn(false).when(userRepository).existsById(anyLong());

        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> service.update(booker.getId(), booker));

        MatcherAssert.assertThat("bad id", equalTo(exception.getMessage()));
    }

}