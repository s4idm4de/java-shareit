package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .id(item.getId())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .request(item.getRequest() != null ? ItemRequestMapper.toRequestDto(item.getRequest()) : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }


    public static Item toItem(ItemDto item) {
        return Item.builder()
                .name(item.getName())
                .id(item.getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserMapper.toUser(item.getOwner()))
                .request(item.getRequest() != null ? ItemRequestMapper.toRequest(item.getRequest()) : null).build();
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemDto(item));
        }

        return result;
    }
}
