package ru.practicum.shareit.requests.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private long requestor;
}
