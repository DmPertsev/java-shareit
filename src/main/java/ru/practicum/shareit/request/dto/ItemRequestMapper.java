package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoShort shortDto) {
        return ItemRequest.builder()
                .description(shortDto.getDescription())
                .created(shortDto.getCreated())
                .build();
    }

    public static ItemRequestDtoShort toShortDto(ItemRequest itemRequest) {
        return ItemRequestDtoShort.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(ItemMapper.toDtoShortList(itemRequest.getItems()))
                .build();
    }

    public static List<ItemRequestDto> toDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::toDto).collect(Collectors.toList());
    }
}