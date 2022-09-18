package ru.practicum.shareit.requests.mapper;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.ItemRequestDtoForResponse;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestMapperTest {
    private Item itemReal;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoForResponse itemRequestDtoForResponse;


    @Mock
    private ItemRepository itemRepository;

    private MockitoSession mockitoSession;
    private ItemRequestMapper itemRequestMapper;


    @BeforeEach
    void init() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestMapper = new ItemRequestMapper(itemRepository);

        itemReal = new Item(2, "knife", "make to cut", true, 3, 1);
        itemRequest = new ItemRequest("cut", 3);
        itemRequestDto = new ItemRequestDto("cut", 3);
        itemRequestDtoForResponse = new ItemRequestDtoForResponse(5, "cut", 3, null, new ArrayList<>());

    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    void toItemRequestDtoForResponse() {
        List<Item> items = List.of(itemReal);
        doReturn(items).when(itemRepository).findAllByRequest(anyLong());

        ItemRequestDtoForResponse response = itemRequestMapper.toItemRequestDtoForResponse(itemRequest);

        MatcherAssert.assertThat(response.getDescription(), equalTo(itemRequestDtoForResponse.getDescription()));
        MatcherAssert.assertThat(response.getRequestor(), equalTo(itemRequestDtoForResponse.getRequestor()));

    }

    @Test
    void toItemRequest() {
        ItemRequest response = itemRequestMapper.toItemRequest(itemRequestDto);

        MatcherAssert.assertThat(response.getDescription(), equalTo(itemRequestDtoForResponse.getDescription()));
        MatcherAssert.assertThat(response.getRequestor(), equalTo(itemRequestDtoForResponse.getRequestor()));
    }
}