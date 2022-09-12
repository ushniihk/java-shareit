package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemAndUser;

import java.util.List;

public interface BookingService {
    BookingDtoWithItemAndUser add(long userId, BookingDto bookingDto);

    BookingDtoWithItemAndUser approve(long userId, long bookingId, boolean approved);

    BookingDtoWithItemAndUser get(long userId, long bookingId);

    List<BookingDtoWithItemAndUser> findAll(long userId, String state, int from, int size);

    List<BookingDtoWithItemAndUser> findAllByItemOwner(Long userId, String state, int from, int size);

}
