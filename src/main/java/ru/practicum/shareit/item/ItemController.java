package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    @Autowired
    private ItemService itemService;
    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto putItem(@Valid @RequestBody ItemDto item, @RequestHeader(requestHeader) Long userId) {
        try {
            return itemService.putItem(item, userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(requestHeader) Long userId,
                              @RequestBody ItemDto item) {
        try {
            return itemService.updateItem(itemId, userId, item);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(requestHeader) Long userId) {
        try {
            return itemService.getItemById(itemId, userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping
    public List<ItemDto> getItemOfUser(@RequestHeader(requestHeader) Long userId,
                                       @RequestParam(required = false) Integer from,
                                       @RequestParam(required = false) Integer size) {
        try {
            if (from != null && from < 0) throw new ValidationException("нет отрицательных индексов");
            return itemService.getItemOfUser(userId, from, size);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(required = false) String text,
                                   @RequestParam(required = false) Integer from,
                                   @RequestParam(required = false) Integer size) {
        try {
            log.info("CONTROLLER searh text {}", text);
            if (from != null && from < 0) throw new ValidationException("нет отрицательных индексов");
            return itemService.getSearch(text, from, size);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto putComment(@PathVariable Long itemId,
                                 @RequestHeader(requestHeader) Long userId,
                                 @RequestBody @Validated CommentDto comment) {

        return itemService.putComment(itemId, userId, comment, LocalDateTime.now());
    }
}
