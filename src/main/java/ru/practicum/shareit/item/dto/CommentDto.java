package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    private Long id;

    @NotNull
    @NotBlank
    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;
}
