package ru.practicum.shareit.ItemRequestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceTest {
    private final EntityManager em;

    private final UserService userService;
    private final ItemRequestController requestController;

    private final ItemRequestService requestService;

    private final ItemService itemService;


    BookingDto bookingDto;

    UserDto userDto;
    ItemDto itemDto;
    UserDto userDto2;
    ItemRequestDto itemRequestDto;

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
        itemRequestDto = ItemRequestDto.builder().description("blabla").build();
    }

    @Test
    void testPutRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                requestService.putRequest(itemRequestDto, 999L, LocalDateTime.now());
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        LocalDateTime time = LocalDateTime.now();
        requestService.putRequest(itemRequestDto, 2L, time);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query.setParameter("id", 1L)
                .getSingleResult();
        assertThat(request.getCreated(), equalTo(time));
        User user = UserMapper.toUser(userDto2);
        user.setId(2L);
        assertThat(request.getRequestor(), equalTo(user));
    }

    @Test
    void testGetRequest() {
        LocalDateTime time = LocalDateTime.now();
        requestService.putRequest(itemRequestDto, 2L, time);
        assertThat(requestController.getRequest(1L, 1L).getRequestorId(), equalTo(2L));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                requestService.getRequest(999L, 999L);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                requestService.getRequest(999L, 1L);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого запроса\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого запроса", exception2.getMessage());
    }

    @Test
    void testGetRequestsAll() {
        LocalDateTime time = LocalDateTime.now();
        requestService.putRequest(itemRequestDto, 2L, time);
        assertEquals(requestController.getRequestsAll(1L, null, null).size(), 1);
        assertEquals(requestController.getRequestsAll(1L, 0, 1).size(), 1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                requestController.getRequestsAll(999L, 0, 1);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
    }

    @Test
    void testGetRequests() {
        LocalDateTime time = LocalDateTime.now();
        requestService.putRequest(itemRequestDto, 2L, time);
        assertEquals(requestController.getRequests(2L).size(), 1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                requestController.getRequests(999L);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
    }
}
