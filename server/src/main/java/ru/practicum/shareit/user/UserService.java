package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ContradictionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    @Autowired
    private final UserRepository repository;

    public List<UserDto> getAll() {
        log.info("UserService getALL");
        List<User> users = repository.findAll();
        log.info("UserService getAll {}", users);
        return UserMapper.toUserDto(users);
    }

    public UserDto getUserById(long userId) {
        try {
            User user = repository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            return UserMapper.toUserDto(user);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    public UserDto addUser(UserDto user) {

        User userFromDto = repository.save(UserMapper.toUser(user));
        log.info("UserService addUser {}", repository.findAll());
        return UserMapper.toUserDto(userFromDto);

    }

    public UserDto updateUser(UserDto user, Long userId) {
        try {
            log.info("UserService updateUser {}", user);
            User oldUser = repository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            if (user.getEmail() != null && !user.getEmail().isBlank() && !oldUser.getEmail().equals(user.getEmail())) {
                List<User> users = repository.findAllByEmail(user.getEmail());
                if (users.size() != 0) {
                    throw new ContradictionException("мэил должен быть уникальным");
                }
            } else {
                user.setEmail(oldUser.getEmail());
            }
            if (user.getName() == null || user.getName().isBlank()) user.setName(oldUser.getName());
            User userForAdd = UserMapper.toUser(user);
            userForAdd.setId(userId);
            User userForLog = repository.save(userForAdd);
            log.info("UserService updateUser {}", userForLog);
            return UserMapper.toUserDto(userForLog);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ContradictionException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    public void delete(Long userId) {
        try {
            User user = repository.findById(userId).orElseThrow(()
                    -> new NotFoundException("нет такого пользователя"));
            repository.delete(user);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
