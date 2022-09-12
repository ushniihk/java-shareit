package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@Entity
public class ItemRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "requestor_id", nullable = false)
    private long requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public ItemRequest(String description, long requestor) {
        this.description = description;
        this.requestor = requestor;
        created = LocalDateTime.now();
    }
}
