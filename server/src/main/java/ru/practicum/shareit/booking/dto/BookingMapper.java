package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .status(bookingDto.getStatus())
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .build();
    }

    public static List<BookingDto> toBookingDto(List<Booking> bookings) {
        return bookings.stream().map(booking -> toBookingDto(booking)).collect(Collectors.toList());
    }
}
