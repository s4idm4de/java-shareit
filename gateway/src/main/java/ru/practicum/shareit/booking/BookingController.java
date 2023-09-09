package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    @Autowired
    private final BookingClient bookingClient;

    private final String requestHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) throws IllegalException {
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
            return bookingClient.getBookings(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            log.info("Mistake {}", e.getMessage());
            throw new IllegalException("Unknown state: " + stateParam);
        }
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        try {
            log.info("Creating booking {}, userId={}", requestDto, userId);
            if (requestDto.getEnd().isBefore(requestDto.getStart())
                    || requestDto.getStart().equals(requestDto.getEnd()))
                throw new ValidationException("время начала резервации должно быть строго раньше времени конца");
            return bookingClient.bookItem(userId, requestDto);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam boolean approved) {
        log.info("gadge Controller update");
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) throws IllegalException {
        try {
            BookingState state = BookingState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
            log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
            return bookingClient.getAllBookingsOfOwner(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            log.info("Mistake {}", e.getMessage());
            throw new IllegalException("Unknown state: " + stateParam);
        }
    }
}
