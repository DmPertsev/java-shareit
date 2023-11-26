package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDtoResponse> getByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    ItemDto getById(Long itemId, Long userId);

    ItemDto create(Long userId, CreateItemDto itemDto);

    ItemDto update(Long itemId, Long userId, UpdateItemDto itemDto);

    ItemDto delete(Long itemId);

    CommentDto createComment(Long itemId, Long userId, CreateCommentDto commentDto);
}