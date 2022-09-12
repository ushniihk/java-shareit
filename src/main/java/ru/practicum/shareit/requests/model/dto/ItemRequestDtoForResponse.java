package ru.practicum.shareit.requests.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoForResponse {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
    List<ItemDtoWithBooking> items;
}
