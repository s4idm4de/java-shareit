package ru.practicum.shareit.BookingTest;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IllegalException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
//@Rollback(false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceTest {
    private final EntityManager em;

    private final UserService userService;

    private final UserRepository userRepository;

    private final ItemService itemService;


    private final ItemController itemController;


    private final ItemRepository itemRepository;


    private final BookingService bookingService;


    private final BookingController bookingController;

    BookingDto bookingDto;

    UserDto userDto;
    ItemDto itemDto;
    UserDto userDto2;

    @BeforeEach
    void setParams() throws Exception {
        userDto = UserDto.builder().email("email@mail.ru").name("Name").build();
        userDto2 = UserDto.builder().email("email2@mail.ru").name("Name2").build();
        itemDto = ItemDto.builder().available(true).name("Name").description("description").owner(userDto).build();
        bookingDto = BookingDto.builder().itemId(1L)
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();
        userService.addUser(userDto);
        userService.addUser(userDto2);
        itemService.putItem(itemDto, 1L);
    }

    @Test
    void approveBooking() {
        bookingService.putBooking(bookingDto, 2L);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingController.approveBooking(999L, 999L, true);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого booking\"; nested exception is " +
                "ru.practicum.shareit.exception.NotFoundException: нет такого booking", exception.getMessage());

        bookingService.approveBooking(1L, 1L, true);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L)
                .getSingleResult();
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingController.approveBooking(1L, 1L, true);
            }
        });
        assertEquals("400 BAD_REQUEST \"нельзя менять статус на такой же\"; nested exception is " +
                "ru.practicum.shareit.exception.ValidationException: нельзя менять статус " +
                "на такой же", exception2.getMessage());
    }

    @Test
    void testPutBooking() throws Exception {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingController.putBooking(999L, bookingDto);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());


        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingService.putBooking(bookingDto, 1L);
            }
        });
        assertEquals("404 NOT_FOUND \"ну не себе же сдавать в аренду\"; nested exception" +
                " is ru.practicum.shareit.exception.NotFoundException:" +
                " ну не себе же сдавать в аренду", exception2.getMessage());

        bookingService.putBooking(bookingDto, 2L);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L)
                .getSingleResult();
        assertThat(booking.getId(), equalTo(1L));
        itemDto.setId(1L);
        assertThat(booking.getItem(), equalTo(ItemMapper.toItem(itemDto)));
        bookingDto.setItemId(9999L);
        ResponseStatusException exception3 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingController.putBooking(1L, bookingDto);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого item\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого item", exception3.getMessage());
    }

    @Test
    void getAllBookings() throws Exception {
        bookingService.putBooking(bookingDto, 2L);
        assertEquals(bookingController.getAllBookings(2L, "ALL", null, null).get(0).getId(),
                1L);
        assertEquals(bookingService.getAllBookings(1L, "ALL", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookings(2L, "ALL", 0, 1).get(0).getId(),
                1L);
        assertEquals(bookingService.getAllBookings(2L, "FUTURE", null, null).size(),
                1);
        assertEquals(bookingService.getAllBookings(2L, "PAST", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookings(2L, "CURRENT", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookings(2L, "FUTURE", 0, 1).size(),
                1);
        assertEquals(bookingService.getAllBookings(2L, "PAST", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookings(2L, "CURRENT", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookings(1L, "CANCELED", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookings(1L, "CANCELED", null, null).size(),
                0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException, IllegalException {
                bookingController.getAllBookings(9999L, "ALL", null, null);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());

        IllegalException exception2 = assertThrows(IllegalException.class, new Executable() {
            @Override
            public void execute() throws IllegalException {
                bookingController.getAllBookings(2L, "ILLEGAL", null, null);
            }
        });
        assertEquals("Unknown state: ILLEGAL", exception2.getMessage());
    }

    @Test
    void getAllBookingsOfOwner() throws Exception {
        bookingService.putBooking(bookingDto, 2L);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "ALL", null, null).get(0).getId(),
                1L);
        assertEquals(bookingService.getAllBookingsOfOwner(2L, "ALL", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "ALL", 0, 1).get(0).getId(),
                1L);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "FUTURE", null, null).size(),
                1);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "PAST", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "CURRENT", null, null).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "FUTURE", 0, 1).size(),
                1);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "PAST", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "CURRENT", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "CANCELED", 0, 1).size(),
                0);
        assertEquals(bookingService.getAllBookingsOfOwner(1L, "CANCELED", null, null).size(),
                0);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException, IllegalException {
                bookingController.getAllBookingsOfOwner(9999L, "ALL", null, null);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());

        IllegalException exception2 = assertThrows(IllegalException.class, new Executable() {
            @Override
            public void execute() throws IllegalException {
                bookingController.getAllBookingsOfOwner(2L, "ILLEGAL", null, null);
            }
        });
        assertEquals("Unknown state: ILLEGAL", exception2.getMessage());
    }

    @Test
    void getBooking() throws NotFoundException {
        bookingService.putBooking(bookingDto, 2L);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                bookingController.getBooking(999L, 999L);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        assertEquals(bookingService.getBooking(1L, 1L).getId(), 1L);
    }
}
