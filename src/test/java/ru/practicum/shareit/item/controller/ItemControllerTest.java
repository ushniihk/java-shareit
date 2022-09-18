package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoWithAuthorAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService service;

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    private ItemDto dto;
    private ItemDtoWithBooking itemDtoWithBooking;

    @BeforeEach
    void setUp() {
        dto = new ItemDto(
                1L,
                "first item",
                "simple item",
                true,
                2,
                3
        );

        itemDtoWithBooking = new ItemDtoWithBooking(
                1L,
                "first item",
                "simple item",
                true,
                2,
                3,
                null,
                null,
                null
        );
    }

    @Test
    void shouldGetAllByUser() throws Exception {
        List<ItemDtoWithBooking> list = new ArrayList<>();
        list.add(itemDtoWithBooking);
        when(service.getAllByUser(any(long.class), any(int.class), any(int.class)))
                .thenReturn(new PageImpl<>(list));
        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), long.class))
                .andExpect(jsonPath("$[0].name", is(list.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(list.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].owner", is((int) list.get(0).getOwner())))
                .andExpect(jsonPath("$[0].requestId", is((int) list.get(0).getRequestId())));
    }

    @Test
    void shouldGet() throws Exception {
        when(service.get(any(long.class), any(long.class)))
                .thenReturn(itemDtoWithBooking);
        mvc.perform(MockMvcRequestBuilders.get("/items/9")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$.owner", is((int) itemDtoWithBooking.getOwner())))
                .andExpect(jsonPath("$.requestId", is((int) itemDtoWithBooking.getRequestId())));
    }

    @Test
    void shouldSearch() throws Exception {
        List<ItemDto> list = new ArrayList<>();
        list.add(dto);
        when(service.search(any(long.class), any(String.class), any(int.class), any(int.class)))
                .thenReturn(list);
        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=sometext")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), long.class))
                .andExpect(jsonPath("$[0].name", is(list.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(list.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].owner", is((int) list.get(0).getOwner())))
                .andExpect(jsonPath("$[0].requestId", is((int) list.get(0).getRequestId())));
    }

    @Test
    void shouldAdd() throws Exception {
        when(service.addNew(any(long.class), any(ItemDto.class)))
                .thenReturn(itemDtoWithBooking);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$.owner", is((int) itemDtoWithBooking.getOwner())))
                .andExpect(jsonPath("$.requestId", is((int) itemDtoWithBooking.getRequestId())));
    }

    @Test
    void shouldUpdate() throws Exception {
        when(service.update(any(long.class), any(long.class), any(ItemDto.class)))
                .thenReturn(dto);

        mvc.perform(patch("/items/9")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.owner", is((int) dto.getOwner())))
                .andExpect(jsonPath("$.requestId", is((int) dto.getRequestId())));
    }

    @Test
    void shouldDelete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/items/9")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentDto = new CommentDto(1, "text", 2, 3, null);
        CommentDtoWithAuthorAndItem commentDtoForResponse =
                new CommentDtoWithAuthorAndItem(1, "text", 2, "Sam", null);

        when(service.addComment(any(long.class), any(long.class), any(CommentDto.class)))
                .thenReturn(commentDtoForResponse);

        mvc.perform(post("/items/9/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.text", is(commentDtoForResponse.getText())))
                .andExpect(jsonPath("$.itemId", is((int) commentDtoForResponse.getItemId())))
                .andExpect(jsonPath("$.authorName", is(commentDtoForResponse.getAuthorName())));
    }
}