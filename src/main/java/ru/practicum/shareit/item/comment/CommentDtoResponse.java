package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoResponse {

    private Long id;

    private String text;

    private ItemComment item;

    private Long authorId;

    private String authorName;

    private LocalDateTime created;

    @Data
    public static class ItemComment {
        private final Long id;

        private final String name;
    }
}