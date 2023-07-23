package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    public ItemDto putItem(Item item, Integer userId);

    public ItemDto updateItem(Integer itemId, Integer userId, Item item);

    public ItemDto getItemById(Integer itemId, Integer userId);

    public List<ItemDto> getItemOfUser(Integer userId);

    public List<ItemDto> getSearch(String text);
}
