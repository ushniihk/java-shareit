package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private List<CommentDtoWithAuthorAndItem> comments;

    public ItemDtoWithBooking(long id, String name, String description, Boolean available, Long owner, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = request;
    }
}
