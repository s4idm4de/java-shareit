package ru.practicum.shareit.UserTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private final EntityManager em;
    private final UserService service;
    private final UserController userController;

    private final UserRepository userRepository;

    UserDto userDto;

    @BeforeEach
    void setParams() {
        userDto = UserDto.builder().email("email@mail.ru").name("Name").build();

    }


    @Test
    void saveAndDeleteUser() {
        // given

        // when
        userController.create(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        service.delete(1L);
        TypedQuery<User> query2 = em.createQuery("Select u from User u", User.class);
        List<User> users = query2.getResultList();

        assertThat(users.size(), equalTo(0));
        userRepository.deleteAll();
    }

    @Test
    void updateUser() {
        // given
        service.addUser(userDto);
        UserDto userDto2 = UserDto.builder().email("newmail@mail.ru").name("Nn").build();
        // when
        userController.update(1L, userDto2);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", 1L)
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto2.getName()));
        assertThat(user.getEmail(), equalTo(userDto2.getEmail()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                userController.update(999L, userDto);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        userDto2.setEmail("double@mail.ru");
        userController.create(userDto2);
        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                userController.update(1L, userDto2);
            }
        });
        assertEquals("409 CONFLICT \"мэил должен быть уникальным\"; nested " +
                "exception is ru.practicum.shareit.exception.ContradictionException: " +
                "мэил должен быть уникальным", exception2.getMessage());
    }

    @Test
    void testGetUserById() {
        userController.create(userDto);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                userController.getUserById(999L);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        userDto.setId(1L);
        assertEquals(userController.getUserById(1L), userDto);
    }

    @Test
    void testGetAll() {
        userController.create(userDto);
        assertEquals(userController.getAll().size(), 1);
    }

    @Test
    void testDeleteUser() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, new Executable() {
            @Override
            public void execute() throws ResponseStatusException {
                userController.delete(999L);
            }
        });
        assertEquals("404 NOT_FOUND \"нет такого пользователя\"; nested exception is" +
                " ru.practicum.shareit.exception.NotFoundException: нет такого пользователя", exception.getMessage());
        userController.create(userDto);
        userController.delete(1L);
        assertEquals(userController.getAll().size(), 0);
    }
}
