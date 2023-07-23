package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class Item {
    private Integer id;
    @NotBlank
    private String name;

    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    private User owner;
    private ItemRequest request;

    public Boolean isAvailable() {
        return available;
    }
}
