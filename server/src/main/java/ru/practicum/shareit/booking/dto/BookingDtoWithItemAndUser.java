package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDtoWithItemAndUser {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private Booking.Status status;

    public BookingDtoWithItemAndUser(long id, LocalDateTime start, LocalDateTime end, Booking.Status status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
    }
}
