package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

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
    public BookingDto putBooking(@RequestHeader(requestHeader) Long userId, @RequestBody @Validated BookingDto bookingDto) {
        log.info("BookingController {} LocalDateTime {}", bookingDto, LocalDateTime.now());
        try {
            if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())
                    || bookingDto.getEnd().isBefore(bookingDto.getStart())
                    || bookingDto.getStart().equals(bookingDto.getEnd())) {
                throw new ValidationException("время начала резервации должно быть строго раньше времени конца");
            } else {
                return bookingService.putBooking(bookingDto, userId);
            }
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam boolean approved) {
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
                                           @RequestParam(required = false) String state) throws IllegalException {
        if (state == null) state = "ALL";
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfOwner(@RequestHeader(requestHeader) Long userId,
                                                  @RequestParam(required = false) String state) throws IllegalException {
        if (state == null) state = "ALL";
        return bookingService.getAllBookingsOfOwner(userId, state);
    }
}
