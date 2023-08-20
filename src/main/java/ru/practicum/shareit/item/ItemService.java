package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    public ItemDto putItem(ItemDto item, Long userId) throws NotFoundException;

    public ItemDto updateItem(Long itemId, Long userId, ItemDto item) throws NotFoundException;

    public ItemDto getItemById(Long itemId, Long userId) throws NotFoundException;

    public List<ItemDto> getItemOfUser(Long userId) throws NotFoundException;

    public List<ItemDto> getSearch(String text);

    public CommentDto putComment(Long itemId, Long userId, CommentDto comment, LocalDateTime created);
}
