package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

public interface BookingService {

    public BookingDto putBooking(BookingDto bookingDto, Long userId);

    public BookingDto approveBooking(Long bookingId, Long userId, boolean approved);

    public BookingDto getBooking(Long bookingId, Long userId) throws NotFoundException;

    public List<BookingDto> getAllBookings(Long userId, String state) throws IllegalException;

    public List<BookingDto> getAllBookingsOfOwner(Long userId, String state) throws IllegalException;
}
