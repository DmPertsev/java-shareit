package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedEmailException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return userRepository.findAll(pageable).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toDto(userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет!")));
    }

    @Transactional
    @Override
    public UserDto create(CreateUserDto createUserDto) {
        User newUser = UserMapper.createUserDtoToUser(createUserDto);
        return UserMapper.toDto(userRepository.save(newUser));
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UpdateUserDto updateUserDto) {
        User updateUser = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Такого пользователя нет!"));

        if ((updateUserDto.getEmail() != null) && (!updateUserDto.getEmail().isBlank())) {
            checkIfEmailExists(updateUserDto.getEmail());
            updateUser.setEmail(updateUserDto.getEmail());
        }

        if ((updateUserDto.getName() != null) && (!updateUserDto.getName().isBlank())) {
            updateUser.setName(updateUserDto.getName());
        }

        return UserMapper.toDto(updateUser);
    }

    @Transactional
    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    private void checkIfEmailExists(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new DuplicatedEmailException(email);
        });
    }
}