package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.requests.service.ItemRequestService;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private ItemRequestDto dto;
    private ItemRequestDtoForResponse dtoForResponse;

    @BeforeEach
    void setUp() {
        dto = new ItemRequestDto(
                "Desc",
                1);

        dtoForResponse = new ItemRequestDtoForResponse(
                2,
                "Desc",
                1,
                null,
                new ArrayList<>()
        );
    }

    @Test
    void shouldGetAllByUser() throws Exception {
        List<ItemRequestDtoForResponse> list = new ArrayList<>();
        list.add(dtoForResponse);
        when(service.getAllByUser(any(long.class)))
                .thenReturn(list);
        mvc.perform(MockMvcRequestBuilders.get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$[0].description", is(dtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is((int) dtoForResponse.getRequestor())));
    }

    @Test
    void shouldGetAllByOtherUsers() throws Exception {
        List<ItemRequestDtoForResponse> list = new ArrayList<>();
        list.add(dtoForResponse);
        when(service.getAllByOtherUsers(any(long.class), any(int.class), any(int.class)))
                .thenReturn(list);
        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$[0].description", is(dtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is((int) dtoForResponse.getRequestor())));
    }

    @Test
    void shouldGet() throws Exception {
        when(service.get(any(long.class), any(long.class)))
                .thenReturn(dtoForResponse);
        mvc.perform(MockMvcRequestBuilders.get("/requests/2")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.description", is(dtoForResponse.getDescription())))
                .andExpect(jsonPath("$.requestor", is((int) dtoForResponse.getRequestor())));
    }

    @Test
    void shouldAdd() throws Exception {
        when(service.add(any(long.class), any(ItemRequestDto.class)))
                .thenReturn(dtoForResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.description", is(dtoForResponse.getDescription())))
                .andExpect(jsonPath("$.requestor", is((int) dtoForResponse.getRequestor())));
    }
}