package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .created(itemRequestDto.getCreated())
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .build();
    }

    public static List<ItemRequestDto> toRequestDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDto> requestsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestsDto.add(toRequestDto(request));
        }
        return requestsDto;
    }
}
