package ru.practicum.shareit.UserTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private final EntityManager em;
    private final UserService service;

    private final UserRepository userRepository;

    UserDto userDto;

    @BeforeEach
    void setParams() {
        userDto = UserDto.builder().email("email@mail.ru").name("Name").build();

    }

    @AfterEach
    void afterEach() {

    }

    @Test
    void saveAndDeleteUser() {
        // given

        // when
        service.addUser(userDto);

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
        UserDto userDto2 = UserDto.builder().email("newmail@mail.ru").build();
        // when
        service.updateUser(userDto2, 1L);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", "newmail@mail.ru")
                .getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto2.getEmail()));
        userRepository.deleteAll();
    }
    

}
