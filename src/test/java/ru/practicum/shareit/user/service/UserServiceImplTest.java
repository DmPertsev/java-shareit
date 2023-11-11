package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.DuplicatedEmailException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_shouldReturnListNotEmpty() {
        User user1 = new User(1L, "Alex", "alex.b@yandex.ru");

        User user2 = new User(2L, "Bill", "bill.d@yandex.ru");

        User user3 = new User(3L, "John", "john.d@yandex.ru");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> page = new PageImpl<>(List.of(user1, user2, user3));

        Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

        List<UserDto> expectedList = Stream.of(user1, user2, user3).map(UserMapper::toDto).collect(Collectors.toList());
        List<UserDto> actualList = userService.getAll(0, 10);

        assertEquals(expectedList, actualList);
    }

    @Test
    void getById_shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1L)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser() {
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        UserDto expected = new UserDto(1L, "Alex", "alex.b@yandex.ru");
        UserDto actual = userService.getById(user.getId());

        assertEquals(expected, actual);
    }

    @Test
    void create_shouldSaveUserDto() {
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        CreateUserDto userDtoWithoutId = new CreateUserDto(null, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto expected = new UserDto(1L, "Alex", "alex.b@yandex.ru");
        UserDto actual = userService.create(userDtoWithoutId);

        assertEquals(expected, actual);
    }

    @Test
    void createWithEmailNotValid_shouldReturnValidationException() {
        CreateUserDto user = new CreateUserDto(null, "Alex", "email");
        Mockito.when(userRepository.save(Mockito.any())).thenThrow(ValidationException.class);

        assertThatThrownBy(() -> userService.create(user)).isInstanceOf(ValidationException.class);
    }


    @Test
    void update_shouldReturnUserNotFoundException() {
        UpdateUserDto dto = new UpdateUserDto(999L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());
        Long userId = 999L;

        assertThatThrownBy(() -> userService.update(userId, dto)).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void update_shouldUpdateName() {
        UpdateUserDto dto = new UpdateUserDto(null, "Alex", "john.d@yandex.ru");
        Long userId = 1L;
        User user = new User(1L, "Alex", "alex.b@yandex.ru");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto expected = new UserDto(1L, "Alex", "john.d@yandex.ru");
        UserDto actual = userService.update(userId, dto);

        assertEquals(expected, actual);
    }

    @Test
    void update_shouldThrowDuplicateEmailException() {
        Long id = 1L;
        String newEmail = "newEmail@test.test";

        UpdateUserDto dto = new UpdateUserDto(1L, "Alex", newEmail);
        User user = new User(1L, "Alex", "oldEmail@test.test");

        // Возвращаем пользователя с известным идентификатором
        Mockito.when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(new User(2L, "AnotherUser", newEmail)));

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.update(id, dto))
                .isInstanceOf(DuplicatedEmailException.class)
                .hasMessageContaining(newEmail);
    }

    @Test
    void delete() {
        User user4 = new User(4L, "Mike", "mike.d@yandex.ru");

        userService.deleteById(user4.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(4L);
    }

    @Test
    void update_shouldReturnUnchangedUserDto() {
        Long userId = 1L;
        UpdateUserDto dto = new UpdateUserDto(null, null, null);

        User user = new User(userId, "John", "john.d@yandex.ru");
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto expected = UserMapper.toDto(user);
        UserDto actual = userService.update(userId, dto);

        assertEquals(expected, actual);
    }

    @Test
    void deleteById_shouldCallDeleteByIdWithExpectedId() {
        Long userId = 1L;
        userService.deleteById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }
}