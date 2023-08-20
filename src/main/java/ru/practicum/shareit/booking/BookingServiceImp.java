package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;


    @Override
    public BookingDto putBooking(BookingDto bookingDto, Long userId) {
        log.info("BookingService putBooking {} LocalNow {}", bookingDto, LocalDateTime.now());
        try {

            bookingDto.setStatus(BookingStatus.WAITING);
            Booking booking = BookingMapper.toBooking(bookingDto);
            User booker = userRepository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(()
                    -> new NotFoundException("нет такого item"));
            if (item.isAvailable() && !item.getOwner().getId().equals(userId)) {
                booking.setItem(item);
                booking.setBooker(booker);
                log.info("BookingService putBooking {}", booking);
                return BookingMapper.toBookingDto(bookingRepository.save(booking));
            } else if (item.getOwner().getId().equals(userId)) {
                throw new NotFoundException("ну не себе же сдавать в аренду");
            } else {
                throw new ValidationException("вещь недоступна для брони");
            }
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, boolean approved) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                    -> new NotFoundException("нет такого booking"));
            if (itemRepository.findByOwner_Id(userId).contains(booking.getItem())) {
                if (approved && !booking.getStatus().equals(BookingStatus.APPROVED)) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else if (!approved && !booking.getStatus().equals(BookingStatus.REJECTED)) {
                    booking.setStatus(BookingStatus.REJECTED);
                } else {
                    throw new ValidationException("нельзя менять статус на такой же");
                }
                log.info("BookingService approveBooking {}", bookingRepository.findAll());
                return BookingMapper.toBookingDto(bookingRepository.save(booking));

            } else {
                throw new NotFoundException("нет такого пользователя");
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ValidationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(()
                        -> new NotFoundException("нет такого пользователя"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()
                        -> new NotFoundException("нет такого бронирования"));
        if (booking.getItem().getOwner().equals(user) || booking.getBooker().equals(user)) {
            log.info("BookingService getBooking {}", bookingRepository.findAll());
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("доступ только у арендатора или хозяина");
        }
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, String state) throws IllegalException {
        try {
            userRepository.findById(userId)
                    .orElseThrow(()
                            -> new NotFoundException("нет такого пользователя"));
            BookingStatusForSearch statusForSearch = BookingStatusForSearch.valueOf(state);
            if (statusForSearch.equals(BookingStatusForSearch.ALL)) {
                return BookingMapper.toBookingDto(
                        bookingRepository.findAllByBooker_Id(userId,
                                Sort.by(Sort.Direction.DESC, "start")));
            } else if (statusForSearch.equals(BookingStatusForSearch.FUTURE)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByBooker_IdAndStartIsAfter(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            } else if (statusForSearch.equals(BookingStatusForSearch.CURRENT)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByBooker_IdAndEndIsAfterAndStartIsBefore(userId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            } else if (statusForSearch.equals(BookingStatusForSearch.PAST)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByBooker_IdAndEndIsBefore(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            } else {
                BookingStatus stateForSearch = BookingStatus.valueOf(state);
                log.info("BookingService getAllBookingsOfOwner {}", state);
                return BookingMapper.toBookingDto(
                        bookingRepository.findAllByBooker_IdAndStatus(userId, stateForSearch,
                                Sort.by(Sort.Direction.DESC, "start")));
            }
        } catch (IllegalArgumentException e) {
            log.info("Mistake {}", e.getMessage());
            throw new IllegalException("Unknown state: " + state);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsOfOwner(Long userId, String state) throws IllegalException {
        try {
            userRepository.findById(userId)
                    .orElseThrow(()
                            -> new NotFoundException("нет такого пользователя"));
            BookingStatusForSearch statusForSearch = BookingStatusForSearch.valueOf(state);
            if (statusForSearch.equals(BookingStatusForSearch.ALL)) {
                return BookingMapper.toBookingDto(
                        bookingRepository.findAll().stream().filter(booking -> booking.getItem()
                                        .getOwner().getId().equals(userId))
                                .sorted(Comparator.comparing(Booking::getStart).reversed())
                                .collect(Collectors.toList()));
            } else if (statusForSearch.equals(BookingStatusForSearch.FUTURE)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByStartIsAfter(LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .filter(booking -> booking.getItem()
                                .getOwner().getId().equals(userId)).collect(Collectors.toList()));
            } else if (statusForSearch.equals(BookingStatusForSearch.CURRENT)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByEndIsAfterAndStartIsBefore(
                                LocalDateTime.now(), LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .filter(booking -> booking.getItem()
                                .getOwner().getId().equals(userId)).collect(Collectors.toList()));
            } else if (statusForSearch.equals(BookingStatusForSearch.PAST)) {
                return BookingMapper.toBookingDto(bookingRepository.findAllByEndIsBefore(LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start")).stream()
                        .filter(booking -> booking.getItem()
                                .getOwner().getId().equals(userId)).collect(Collectors.toList()));
            } else {
                BookingStatus stateForSearch = BookingStatus.valueOf(state);
                log.info("BookingService getAllBookingsOfOwner {}", stateForSearch);
                return BookingMapper.toBookingDto(
                        bookingRepository.findAllByStatus(stateForSearch).stream().filter(booking -> booking.getItem()
                                        .getOwner().getId().equals(userId))
                                .sorted(Comparator.comparing(Booking::getStart).reversed())
                                .collect(Collectors.toList()));
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {

            log.info("Mistake {}", e.getMessage());
            throw new IllegalException("Unknown state: " + state);
        }
    }
}
