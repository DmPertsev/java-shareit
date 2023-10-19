package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto comment(Long userId, Long itemId, CommentDto commentDto);
    List<CommentDto> commentDtos(Long itemId);
}