package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователя нет: " + userId)));
    }

    @Transactional
    @Override
    public UserDto create(CreateUserDto createUserDto) {
        User newUser = UserMapper.createUserDtoToUser(createUserDto);
        userRepository.save(newUser);

        return UserMapper.toDto(newUser);
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UpdateUserDto updateUserDto) {
        User updateUser = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователя нет: " + userId));

        if ((updateUserDto.getEmail() != null) && (!updateUserDto.getEmail().isBlank())) {
            updateUser.setEmail(updateUserDto.getEmail());
        }

        if ((updateUserDto.getName() != null) && (!updateUserDto.getName().isBlank())) {
            updateUser.setName(updateUserDto.getName());
        }

        return UserMapper.toDto(updateUser);
    }

    @Transactional
    @Override
    public UserDto deleteById(Long userId) {
        User delUser = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователя нет: " + userId));
        userRepository.deleteById(userId);

        return UserMapper.toDto(delUser);
    }

    @Transactional(readOnly = true)
    public void throwIfNotExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователя нет: " + userId);
        }
    }
}