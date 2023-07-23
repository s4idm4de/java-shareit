package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder(toBuilder = true)
public class BookingDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
}
