package ru.practicum.shareit.ItemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
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

    private final ItemRepository itemRepository;
    UserDto userDto;
    ItemDto itemDto;


    @BeforeEach
    void setParams() throws Exception {
        userDto = UserDto.builder().email("email@mail.ru").name("Name").build();
        itemDto = ItemDto.builder().available(true).name("Name").description("description").owner(userDto).build();
        userService.addUser(userDto);
        itemService.putItem(itemDto, 1L);
    }


    @Test
    void testSearch() throws Exception {
        UserDto userDto2 = UserDto.builder().email("email2@mail.ru").name("Name2").build();
        userService.addUser(userDto2);
        ItemDto itemDto2 = ItemDto.builder().available(true).name("Name2").description("d2").owner(userDto2).build();
        itemService.putItem(itemDto2, 1L);
        itemDto2.setId(2L);
        assertArrayEquals(new List[]{itemService.getSearch("d2")}, new List[]{List.of(itemDto2)});
    }


    @Test
    void testAddItem() throws Exception {

        // then
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDto.getName()));
    }

    @Test
    void testNotFoundException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                itemController.putItem(itemDto, 999L);
            }
        });

        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
    }

    @Test
    void getItemById() throws Exception {
        ItemDto item = itemService.getItemById(1L, 1L);
        itemDto.setId(1L);
        itemDto.setComments(new ArrayList<>());
        itemDto.getOwner().setId(1L);
        assertThat(item, equalTo(itemDto));
    }

}
