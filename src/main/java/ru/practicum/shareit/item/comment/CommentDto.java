package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CommentDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}