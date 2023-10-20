package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoShortResponse;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemDtoResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoShortResponse lastBooking;

    private BookingDtoShortResponse nextBooking;

    private List<CommentDtoResponse> comments;
}