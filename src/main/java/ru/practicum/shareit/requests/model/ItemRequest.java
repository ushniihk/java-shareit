package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private long id;
    String description;
    User requestor;
    LocalDateTime created;
}
