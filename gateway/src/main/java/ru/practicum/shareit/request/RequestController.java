package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    @Autowired
    private RequestClient requestClient;
    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> putRequest(@RequestBody @Validated RequestDto requestDto,
                                             @RequestHeader(requestHeader) Long userId) {
        return requestClient.putRequest(requestDto, userId);
    }

    @GetMapping //список своих запросов с ответами на них
    public ResponseEntity<Object> getRequests(@RequestHeader(requestHeader) Long userId) {
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader(requestHeader) Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestClient.getRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(requestHeader) Long userId, @PathVariable Long requestId) {
        return requestClient.getRequest(requestId, userId);
    }

}
