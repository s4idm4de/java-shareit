package ru.practicum.shareit.user.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserMapper {
    public static UserDto toUserDto(User user) {
        log.info("UserMapper toUserDto {}", user);
        return UserDto.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }

    public static User toUser(UserDto user) {
        log.info("UserMapper toUser {}", user);
        return User.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }

    public static List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }
        log.info("UserMapper toUser {}", users);
        return result;
    }
}
