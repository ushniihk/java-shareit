package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoWithAuthorAndItem {
    private long id;
    private String text;
    private long itemId;
    private String authorName;
    private LocalDate created;

    public CommentDtoWithAuthorAndItem(long id, String text, long itemId, LocalDate created) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.created = created;
    }

}
