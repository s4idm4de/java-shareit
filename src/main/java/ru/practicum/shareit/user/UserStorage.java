package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ContradictionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserStorage {
    private long id = 1;
    private HashMap<Long, User> users = new HashMap<>();

    public List<UserDto> getAll() {
        return users.values().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    public User getUserById(Long userId) throws NotFoundException {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("Нет пользователя " + userId);
        }
    }

    public User addUser(User user) throws ContradictionException {
        if (users.values().stream()
                .noneMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            user.setId(id);
            users.put(id, user);
            id++;
            return user;
        } else {
            throw new ContradictionException("мэил должен быть уникальным");
        }
    }

    public User updateUser(User user, Long userId) throws NotFoundException, ContradictionException {
        if (users.containsKey(userId)) {
            User oldUser = users.get(userId);
            if (user.getName() == null || user.getName().isBlank()) user.setName(oldUser.getName());
            if (user.getEmail() == null || user.getEmail().isBlank()) user.setEmail(oldUser.getEmail());
            log.info("UPDATE StoRAGE {}", user);
            @Valid User userForAdd = user;
            userForAdd.setId(userId);
            List<User> usersWithEmail = users.values().stream()
                    .filter(user2 -> user2.getEmail().equals(userForAdd.getEmail()))
                    .collect(Collectors.toList());
            if (usersWithEmail.size() > 0 && !usersWithEmail.get(0).getId().equals(userId)) {
                throw new ContradictionException("мэил уже занят");
            } else {
                users.put(userId, userForAdd);
                return users.get(userId);
            }
        } else {
            throw new NotFoundException("нет такого пользователя");
        }
    }

    public void delete(Long userId) throws NotFoundException {
        if (users.containsKey(userId)) {
            users.remove(userId);
        } else {
            throw new NotFoundException("нет такого пользователя");
        }
    }
}
