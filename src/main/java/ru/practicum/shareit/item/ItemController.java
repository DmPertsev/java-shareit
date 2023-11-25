package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId,
                           @RequestHeader(name = Variables.HEADER, required = false) Long userId) {
        log.info("Получен запрос GET /items/{}.", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /items/search?from={}&size={}.", from, size);
        return itemService.search(text, from, size);
    }

    @GetMapping
    public List<ItemDtoResponse> getByUserId(@RequestHeader(name = Variables.HEADER) Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос GET /items?from={}&size={}.", from, size);
        return itemService.getByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(name = Variables.HEADER) Long userId,
                          @Valid @RequestBody CreateItemDto itemDto) {
        log.info("Получен запрос POST /items.");
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@PathVariable Long id,
                                    @RequestHeader(name = Variables.HEADER) Long userId,
                                    @Valid @RequestBody CreateCommentDto commentDto) {
        log.info("Получен запрос POST /items/{id}/comment.");
        return itemService.createComment(id, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader(name = Variables.HEADER) Long userId,
                          @Valid @RequestBody UpdateItemDto itemDto) {
        log.debug("Получен запрос PATCH /items/{}.", itemId);
        return itemService.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ItemDto delete(@PathVariable Long id) {
        log.debug("Получен запрос DELETE /items/{}.", id);
        return itemService.delete(id);
    }
}