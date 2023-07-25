package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }

    public static User toUser(UserDto user) {
        return User.builder().name(user.getName()).email(user.getEmail()).id(user.getId()).build();
    }
}
