package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class UpdateItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
}