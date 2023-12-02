package ru.practicum.shareit.user.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }

    public static User toUser(UserDto user) {
        return User.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }

    public static List<UserDto> toUserDto(List<User> users) {
        return users.stream().map(user -> toUserDto(user)).collect(Collectors.toList());
    }
}
