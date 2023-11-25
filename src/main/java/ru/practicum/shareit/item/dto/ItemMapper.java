package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static Item createItemDtoToItem(CreateItemDto createItemDto) {
        return new Item(
                createItemDto.getId(),
                createItemDto.getName(),
                createItemDto.getDescription(),
                createItemDto.getAvailable(),
                null,
                null,
                null,
                null,
                null
        );
    }

    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .comments(item.getComments())
                .requestId((item.getItemRequest() == null ? null : item.getItemRequest().getId()))
                .build();
    }

    public static ItemDtoResponse toDtoResponse(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static UpdateItemDto toUpdateDto(Item item) {
        return UpdateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId((item.getItemRequest() == null ? null : item.getItemRequest().getId()))
                .build();
    }

    public static List<UpdateItemDto> toDtoShortList(List<Item> items) {
        return items.stream().map(ItemMapper::toUpdateDto).collect(Collectors.toList());
    }
}