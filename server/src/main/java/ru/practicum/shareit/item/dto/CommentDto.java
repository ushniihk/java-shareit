package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private long itemId;
    private long authorId;
    private LocalDate created;
}
