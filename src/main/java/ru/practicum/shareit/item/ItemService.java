package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto putItem(ItemDto item, Integer userId);

    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item);

    public ItemDto getItemById(Integer itemId, Integer userId);

    public List<ItemDto> getItemOfUser(Integer userId);

    public List<ItemDto> getSearch(String text);
}
