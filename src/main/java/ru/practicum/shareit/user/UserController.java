package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto create(@Validated @RequestBody UserDto user) {

        return userService.addUser(user);

    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto user) {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        userService.delete(userId);
    }
}
