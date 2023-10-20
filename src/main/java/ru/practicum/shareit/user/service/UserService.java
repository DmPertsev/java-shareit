package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(CreateUserDto createUserDto);

    UserDto update(Long userId, UpdateUserDto updateUserDto);

    UserDto deleteById(Long userId);
}