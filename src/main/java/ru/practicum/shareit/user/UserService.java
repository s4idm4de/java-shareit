package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ContradictionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserStorage userStorage;

    public List<UserDto> getAll() {
        return userStorage.getAll();
    }

    public UserDto getUserById(Integer userId) {
        try {
            return UserMapper.toUserDto(userStorage.getUserById(userId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public UserDto addUser(UserDto user) {
        try {
            return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(user)));
        } catch (ContradictionException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    public UserDto updateUser(UserDto user, Integer userId) {
        try {
            return UserMapper.toUserDto(userStorage.updateUser(UserMapper.toUser(user), userId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ContradictionException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    public void delete(Integer userId) {
        try {
            userStorage.delete(userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
