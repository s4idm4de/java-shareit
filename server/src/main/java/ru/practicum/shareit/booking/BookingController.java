package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private final String requestHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto putBooking(@RequestHeader(requestHeader) Long userId, @RequestBody BookingDto bookingDto) {
        log.info("BookingController {} LocalDateTime {}", bookingDto, LocalDateTime.now());

        return bookingService.putBooking(bookingDto, userId);

    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam boolean approved) {
        log.info("booking Controller patch");
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        try {
            return bookingService.getBooking(bookingId, userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader(requestHeader) Long userId,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size) throws IllegalException {
        log.info("getAllBookings", state);
        return bookingService.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfOwner(@RequestHeader(requestHeader) Long userId,
                                                  @RequestParam(required = false) String state,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) throws IllegalException {

        return bookingService.getAllBookingsOfOwner(userId, state, from, size);
    }
}
