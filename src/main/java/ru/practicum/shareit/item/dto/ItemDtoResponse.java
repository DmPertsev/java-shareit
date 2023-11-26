package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoShortResponse;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ItemDtoResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoShortResponse lastBooking;

    private BookingDtoShortResponse nextBooking;

    private List<CommentDtoResponse> comments;
}