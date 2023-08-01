package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    @Autowired
    private ItemService itemService;

    @PostMapping
    public ItemDto putItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.putItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(required = false) String text) {
        log.info("CONTROLLER searh text {}", text);
        return itemService.getSearch(text);
    }
}
