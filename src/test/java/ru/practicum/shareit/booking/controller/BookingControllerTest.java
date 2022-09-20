package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndUser;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {


    @MockBean
    private BookingService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private BookingDto dto;
    private BookingDtoWithItemAndUser dtoForResponse;

    @BeforeEach
    void setUp() {
        dto = new BookingDto(
                1L,
                null,
                null,
                2,
                3,
                Booking.Status.WAITING
        );

        dtoForResponse = new BookingDtoWithItemAndUser(
                1L,
                null,
                null,
                null,
                null,
                Booking.Status.APPROVED
        );
    }

    @Test
    void shouldAdd() throws Exception {
        when(service.add(any(long.class), any(BookingDto.class)))
                .thenReturn(dtoForResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.status", is(dtoForResponse.getStatus().toString())));
    }

    @Test
    void shouldApprove() throws Exception {
        when(service.approve(any(long.class), any(long.class), any(boolean.class)))
                .thenReturn(dtoForResponse);

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/9?approved=true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.status", is(dtoForResponse.getStatus().toString())));
    }

    @Test
    void shouldGet() throws Exception {
        when(service.get(any(long.class), any(long.class)))
                .thenReturn(dtoForResponse);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/9")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$.status", is(dtoForResponse.getStatus().toString())));
    }

    @Test
    void shouldFindAll() throws Exception {
        List<BookingDtoWithItemAndUser> list = new ArrayList<>();
        list.add(dtoForResponse);
        when(service.findAll(any(long.class), any(String.class), any(int.class), any(int.class)))
                .thenReturn(list);
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$[0].status", is(dtoForResponse.getStatus().toString())));
    }

    @Test
    void shouldFindAllByItemOwner() throws Exception {
        List<BookingDtoWithItemAndUser> list = new ArrayList<>();
        list.add(dtoForResponse);

        when(service.findAllByItemOwner(any(long.class), any(String.class), any(int.class), any(int.class)))
                .thenReturn(list);

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(list.size())))
                .andExpect(jsonPath("$[0].id", is(dtoForResponse.getId()), long.class))
                .andExpect(jsonPath("$[0].status", is(dtoForResponse.getStatus().toString())));
    }
}