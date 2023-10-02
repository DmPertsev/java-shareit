package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User getById(long id);

    UserDto create(UserDto userDto);

    User update(long id, UserDto user);

    void delete(long id);
}