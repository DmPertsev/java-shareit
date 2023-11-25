package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User createUserDtoToUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setId(createUserDto.getId());
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        return user;
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserDto updateUserDtoToUserDto(UpdateUserDto updateUserDto) {
        UserDto dto = new UserDto();
        dto.setId(updateUserDto.getId());
        dto.setName(updateUserDto.getName());
        dto.setEmail(updateUserDto.getEmail());
        return dto;
    }

    public static UserDto createUserDtoToUserDto(CreateUserDto createUserDto) {
        UserDto dto = new UserDto();
        dto.setId(createUserDto.getId());
        dto.setName(createUserDto.getName());
        dto.setEmail(createUserDto.getEmail());
        return dto;
    }
}