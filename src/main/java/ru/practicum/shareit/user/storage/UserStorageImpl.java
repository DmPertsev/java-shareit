package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageImpl implements UserStorage {
    private final UserMapper userMapper;
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setId(counter++);
        users.put(userDto.getId(), userMapper.toUser(userDto));
        return userDto;
    }

    @Override
    public User update(long id, UserDto user) {
        if (users.containsKey(id)) {
            if (user.getName() != null) {
                users.get(id).setName(user.getName());
            }
            if (user.getEmail() != null) {
                users.get(id).setEmail(user.getEmail());
            }
            return users.get(id);
        } else  {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }
}