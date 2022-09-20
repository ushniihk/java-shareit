package ru.practicum.shareit.requests.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.requests.dao.ItemRequestRepository;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private UserDto booker;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoForResponse itemRequestDtoForResponse;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestService service;

    private MockitoSession mockitoSession;

    @BeforeEach
    void init() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        service = new ItemRequestServiceImpl(itemRequestRepository, itemRequestMapper, userRepository);

        booker = new UserDto(3, "Bob", "bob@mail.ru");
        itemRequest = new ItemRequest("cut", 3);
        itemRequestDto = new ItemRequestDto("cut", 3);
        itemRequestDtoForResponse = new ItemRequestDtoForResponse(
                5, "cut", 3, null, new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    void shouldAdd() {
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(itemRequest).when(itemRequestRepository).save(any(ItemRequest.class));
        doReturn(itemRequest).when(itemRequestMapper).toItemRequest(any(ItemRequestDto.class));
        doReturn(itemRequestDtoForResponse).when(itemRequestMapper).toItemRequestDtoForResponse(any(ItemRequest.class));


        service.add(booker.getId(), itemRequestDto);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequest);
    }

    @Test
    void shouldGetAllByUser() {
        List<ItemRequest> requests = List.of(itemRequest);
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(itemRequestDtoForResponse).when(itemRequestMapper).toItemRequestDtoForResponse(any(ItemRequest.class));
        doReturn(requests).when(itemRequestRepository).findAllByRequestor(anyLong());

        List<ItemRequestDtoForResponse> list = service.getAllByUser(booker.getId());

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(itemRequestDtoForResponse.getId()));
        MatcherAssert.assertThat(list.get(0).getDescription(), equalTo(itemRequestDtoForResponse.getDescription()));
        MatcherAssert.assertThat(list.get(0).getRequestor(), equalTo(itemRequestDtoForResponse.getRequestor()));
        MatcherAssert.assertThat(list.size(), equalTo(1));
    }

    @Test
    void shouldGet() {
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(true).when(itemRequestRepository).existsById(anyLong());
        doReturn(Optional.of(itemRequest)).when(itemRequestRepository).findById(anyLong());
        doReturn(itemRequestDtoForResponse).when(itemRequestMapper).toItemRequestDtoForResponse(any(ItemRequest.class));

        ItemRequestDtoForResponse response = service.get(booker.getId(), itemRequest.getId());

        MatcherAssert.assertThat(response.getId(), equalTo(itemRequestDtoForResponse.getId()));
        MatcherAssert.assertThat(response.getDescription(), equalTo(itemRequestDtoForResponse.getDescription()));
        MatcherAssert.assertThat(response.getRequestor(), equalTo(itemRequestDtoForResponse.getRequestor()));
    }

    @Test
    void shouldGetAllByOtherUsers() {
        List<ItemRequest> requests = List.of(itemRequest);
        Page page = new PageImpl(requests);
        doReturn(true).when(userRepository).existsById(anyLong());
        doReturn(itemRequestDtoForResponse).when(itemRequestMapper).toItemRequestDtoForResponse(any(ItemRequest.class));
        doReturn(page).when(itemRequestRepository).findAllByRequestorIsNot(any(long.class), any(PageRequest.class));

        List<ItemRequestDtoForResponse> list = service.getAllByOtherUsers(booker.getId(), 0, 10);

        MatcherAssert.assertThat(list.get(0).getId(), equalTo(itemRequestDtoForResponse.getId()));
        MatcherAssert.assertThat(list.get(0).getDescription(), equalTo(itemRequestDtoForResponse.getDescription()));
        MatcherAssert.assertThat(list.get(0).getRequestor(), equalTo(itemRequestDtoForResponse.getRequestor()));
        MatcherAssert.assertThat(list.size(), equalTo(1));
    }

    @Test
    void shouldThrowNotFondParameterException() {
        doReturn(false).when(userRepository).existsById(anyLong());

        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> service.add(booker.getId(), itemRequestDto));
        final Exception exception2 = Assertions.assertThrows(
                Exception.class,
                () -> service.getAllByUser(booker.getId()));
        final Exception exception3 = Assertions.assertThrows(
                Exception.class,
                () -> service.get(booker.getId(), itemRequest.getId()));
        final Exception exception4 = Assertions.assertThrows(
                Exception.class,
                () -> service.getAllByOtherUsers(booker.getId(), 0, 10));


        MatcherAssert.assertThat("bad user Id", equalTo(exception.getMessage()));
        MatcherAssert.assertThat("bad user Id", equalTo(exception2.getMessage()));
        MatcherAssert.assertThat("bad user Id", equalTo(exception3.getMessage()));
        MatcherAssert.assertThat("bad user Id", equalTo(exception4.getMessage()));
    }

    @Test
    void shouldThrowIncorrectParameterException() {
        doReturn(true).when(userRepository).existsById(anyLong());
        itemRequestDto.setDescription("");

        final Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> service.add(booker.getId(), itemRequestDto));
        final Exception exception2 = Assertions.assertThrows(
                Exception.class,
                () -> service.getAllByOtherUsers(booker.getId(), 0, 0));

        MatcherAssert.assertThat("empty description", equalTo(exception.getMessage()));
        MatcherAssert.assertThat("bad size or index", equalTo(exception2.getMessage()));

    }


}