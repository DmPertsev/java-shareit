package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        log.info("Пользователи отправлены");
        return userStorage.findAll();
    }

    @Override
    public User getById(long id) {
        log.info("Пользователь с id{} отправлен", id);
        return userStorage.getById(id).orElseThrow(() -> {
            log.warn("User with id {} not found", id);
            throw new ObjectNotFoundException("User not found");
        });
    }

    @Override
    public UserDto create(UserDto userDto) {
        validator(userDto.getEmail(), null);
        log.info("Пользователь создан");
        return userStorage.create(userDto);
    }

    @Override
    public User update(long id, UserDto user) {
        if (user.getEmail() != null) {
            validator(user.getEmail(), id);
        }
        return userStorage.update(id, user);
    }

    @Override
    public void delete(long id) {
        log.info("Пользователь с id {} удалён", id);
        userStorage.delete(id);
    }

    private void validator(String email, Long userId) {
        Collection<User> users = userStorage.findAll();
        if (checker(users, email, userId)) {
            log.warn("Пользователь с таким e-mail уже существует");
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
    }

    private boolean checker(Collection<User> users, String email, Long userId) {
        return users.stream()
                .anyMatch(repoUser -> (userId == null || repoUser.getId() != userId) && repoUser.getEmail().equals(email));
    }
}