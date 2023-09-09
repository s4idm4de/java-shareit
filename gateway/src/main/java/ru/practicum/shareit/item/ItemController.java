package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    @Autowired
    private final ItemClient itemClient;
    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> putItem(@Validated @RequestBody ItemDto item, @RequestHeader(requestHeader) Long userId) {
        return itemClient.putItem(item, userId);

    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader(requestHeader) Long userId,
                                             @RequestBody ItemDto item) {
        @Valid ItemDto itemForValid = ItemDto.builder()
                .id(itemId)
                .name(item.getName() == null ? "Dummy" : item.getName())
                .available(item.getAvailable() == null ? true : item.isAvailable())
                .description(item.getDescription() == null ? "Dummy description" : item.getDescription())
                .build();
        return itemClient.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader(requestHeader) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemOfUser(@RequestHeader(requestHeader) Long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearch(@RequestParam(required = false) String text,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("CONTROLLER searh text {}", text);
        return itemClient.getSearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> putComment(@PathVariable Long itemId,
                                             @RequestHeader(requestHeader) Long userId,
                                             @RequestBody @Validated CommentDto comment) {

        return itemClient.putComment(itemId, userId, comment);
    }
}
