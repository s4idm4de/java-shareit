package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemStorage itemStorage;

    @Override
    public ItemDto putItem(ItemDto item, Integer userId) {
        return ItemMapper.toItemDto(itemStorage.putItem(ItemMapper.toItem(item), userId));
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, userId, ItemMapper.toItem(item)));
    }


    @Override
    public ItemDto getItemById(Integer itemId, Integer userId) {
        try {
            return ItemMapper.toItemDto(itemStorage.getItemById(itemId, userId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }


    @Override
    public List<ItemDto> getItemOfUser(Integer userId) {
        return itemStorage.getItemOfUser(userId);
    }


    @Override
    public List<ItemDto> getSearch(String text) {
        return itemStorage.getSearch(text);
    }

}
