package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Entity
@Table(name = "requests", schema = "public")
@Data
@Builder(toBuilder = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;
    @Column
    private LocalDateTime created;
}
