package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Collection<ItemDto> findAll(long userId);

    Optional<ItemDto> findItem(long itemId);

    Optional<ItemDto> findItemForUpdate(long userId, long itemId);

    Collection<ItemDto> searchItem(String text);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto item);
}