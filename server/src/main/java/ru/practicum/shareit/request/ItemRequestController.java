package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private ItemRequestService requestService;
    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto putRequest(@RequestBody ItemRequestDto requestDto,
                                     @RequestHeader(requestHeader) Long userId) {
        return requestService.putRequest(requestDto, userId, LocalDateTime.now());
    }

    @GetMapping //список своих запросов с ответами на них
    public List<ItemRequestDto> getRequests(@RequestHeader(requestHeader) Long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsAll(@RequestHeader(requestHeader) Long userId, @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        return requestService.getRequestsAll(userId, from, size);

    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(requestHeader) Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(requestId, userId);
    }

}
