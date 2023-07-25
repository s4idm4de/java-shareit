package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemStorage {
    @Autowired
    private UserStorage userStorage;
    private Integer itemId = 1;
    private HashMap<Integer, Item> items = new HashMap<>();

    public Item putItem(Item item, Integer userId) {
        try {
            User user = userStorage.getUserById(userId);
            item.setId(itemId);
            item.setOwner(user);
            items.put(itemId, item);
            itemId++;
            log.info("CREATE items {}", items);
            return item;
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public Item updateItem(Integer itemId, Integer userId, Item item) {
        try {
            if (items.containsKey(itemId) && items.get(itemId).getOwner().getId().equals(userId)) {
                Item oldItem = items.get(itemId);
                @Valid Item itemForAdd = Item.builder()
                        .id(itemId)
                        .owner(item.getOwner() == null ? oldItem.getOwner() : item.getOwner())
                        .name(item.getName() == null ? oldItem.getName() : item.getName())
                        .available(item.getAvailable() == null ? oldItem.isAvailable() : item.isAvailable())
                        .request(item.getRequest() == null ? oldItem.getRequest() : item.getRequest())
                        .description(item.getDescription() == null ? oldItem.getDescription() : item.getDescription())
                        .build();
                items.put(itemId, itemForAdd);
                log.info("UPDATE items {}", items);
                return itemForAdd;
            } else {
                throw new NotFoundException("не найден или пользователь " + userId + "или вещь " + itemId);
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public Item getItemById(Integer itemId, Integer userId) throws NotFoundException {
        log.info("GET ITEM BY ID items {}", items);
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("нет такого itema");
        }
    }

    public List<ItemDto> getItemOfUser(Integer userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId))
                .map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
    }

    public List<ItemDto> getSearch(String text) {
        log.info("Storage getSearch text {}", text);
        if (!(text == null || text.isBlank())) {
            return items.values().stream().filter(item -> filterForSearch(item, text))
                    .map(item -> ItemMapper.toItemDto(item)).collect(Collectors.toList());
        } else {
            log.info("Storage getSearch text is null");
            List<ItemDto> empty = new ArrayList<>();
            return empty;
        }
    }

    private Boolean filterForSearch(Item item, String text) {
        return (item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.isAvailable();
    }
}
