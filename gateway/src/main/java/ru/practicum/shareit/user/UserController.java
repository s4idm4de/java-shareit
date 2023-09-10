package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    @Autowired
    private final UserClient userClient;

    private final String requestHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long userId) {
        log.info("gateway UserController getUserById {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated @RequestBody UserDto user) {
        log.info("gateway UserController create {}", user);
        return userClient.addUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long userId, @RequestBody UserDto user) {
        log.info("gateway UserController update {}", user);
        UserDto dummyUser = UserDto.builder().name("dummyName").email("dummy@mail.ru").build();
        if (user.getName() != null) dummyUser.setName(user.getName());
        if (user.getEmail() != null) dummyUser.setEmail(user.getEmail());
        @Valid UserDto validUser = dummyUser;
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long userId) {
        log.info("gateway UserController delete {}", userId);
        return userClient.delete(userId);
    }
}
