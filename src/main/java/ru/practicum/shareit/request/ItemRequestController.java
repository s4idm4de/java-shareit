package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private ItemRequestService requestService;
    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto putRequest(@RequestBody @Valid ItemRequestDto requestDto,
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
        try {
            if (from != null && from < 0) throw new ValidationException("нет отрицательного индекса");
            return requestService.getRequestsAll(userId, from, size);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader(requestHeader) Long userId, @PathVariable Long requestId) {
        return requestService.getRequest(requestId, userId);
    }

}
