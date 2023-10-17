package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemDto> findAll(long userId);

    ItemDto findItem(long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto item);
}