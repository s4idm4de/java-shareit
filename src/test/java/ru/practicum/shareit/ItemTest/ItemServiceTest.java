package ru.practicum.shareit.ItemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {
    private final EntityManager em;
    private final UserService userService;

    private final ItemService itemService;

    private final ItemController itemController;

    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    ItemRequest itemRequest;
    UserDto userDto;
    ItemDto itemDto;


    @BeforeEach
    void setParams() throws Exception {
        userDto = UserDto.builder().email("email@mail.ru").name("Name").build();
        itemDto = ItemDto.builder().available(true).name("Name").description("description").owner(userDto).build();
        userService.addUser(userDto);
        itemService.putItem(itemDto, 1L);
        itemRequest = ItemRequest.builder().description("lol").build();
    }


    @Test
    void testSearch() throws Exception {
        UserDto userDto2 = UserDto.builder().email("email2@mail.ru").name("Name2").build();
        userService.addUser(userDto2);
        ItemDto itemDto2 = ItemDto.builder().available(true).name("Name2").description("d2").owner(userDto2).build();
        itemService.putItem(itemDto2, 1L);
        itemDto2.setId(2L);
        assertArrayEquals(new List[]{itemService.getSearch("d2", null, null)},
                new List[]{List.of(itemDto2)});
        assertArrayEquals(new List[]{itemService.getSearch("d2", 0, 1)},
                new List[]{List.of(itemDto2)});
        assertEquals(itemController.getSearch(" ", null, null).size(), 0);
    }


    @Test
    void testAddItem() throws Exception {

        // then
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDto.getName()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putItem(itemDto, 999L);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());

        itemDto.setRequestId(1L);

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putItem(itemDto, 1L);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого реквеста\"; nested exception " +
                "is ru.practicum.shareit.exception.NotFoundException: " +
                "нет такого реквеста", exception2.getMessage());

        User user = User.builder().id(1L).build();
        itemRequest.setRequestor(user);
        requestRepository.save(itemRequest);
        assertEquals(itemController.putItem(itemDto, 1L).getRequestId(), 1L);
    }


    @Test
    void testUpdate() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.updateItem(999L, 999L, itemDto);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());

        User user = User.builder().name("Lol").email("aaaa@mail.ru").build();
        userRepository.save(user);

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.updateItem(999L, 1L, itemDto);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такой вещи\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такой вещи", exception2.getMessage());
        itemDto.setDescription("new description");
        itemController.updateItem(1L, 1L, itemDto);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(item.getDescription(), equalTo("new description"));

    }

    @Test
    void getItemById() throws Exception {
        ItemDto item = itemService.getItemById(1L, 1L);
        itemDto.setId(1L);
        itemDto.setComments(new ArrayList<>());
        itemDto.getOwner().setId(1L);
        assertThat(item, equalTo(itemDto));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.getItemById(999L, 1L);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого item\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого item", exception.getMessage());

    }


    @Test
    void testGetItemsOfUser() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.getItemOfUser(999L, null, null);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        assertEquals(itemController.getItemOfUser(1L, null, null).size(), 1);
        assertEquals(itemController.getItemOfUser(1L, 0, 1).size(), 1);
    }

    @Test
    void testPutComment() {
        CommentDto commentDto = CommentDto.builder().text("продам гараж").build();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putComment(999L, 999L, commentDto);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такой вещи\"; nested exception is " +
                "ru.practicum.shareit.exception.NotFoundException: " +
                "нет такой вещи", exception.getMessage());

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putComment(9999L, 1L, commentDto);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такой вещи\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такой вещи", exception2.getMessage());

        ResponseStatusException exception3 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putComment(1L, 1L, commentDto);
            }
        });

        assertEquals("400 BAD_REQUEST \"нельзя оставить отзыв на неиспользованную вещь\"; nested exception " +
                "is javax.validation.ValidationException: " +
                "нельзя оставить отзыв на неиспользованную вещь", exception3.getMessage());
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder().start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).booker(user).build();
        bookingRepository.save(booking);
        itemController.putComment(1L, 1L, commentDto);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", 1L)
                .getSingleResult();
        assertEquals(comment.getText(), "продам гараж");
    }
}
