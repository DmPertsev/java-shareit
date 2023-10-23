package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toComment(CreateCommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static CommentDtoResponse toDtoResponse(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(new CommentDtoResponse.ItemComment(comment.getItem().getId(), comment.getItem().getName()))
                .authorName(comment.getAuthor().getName())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated())
                .build();
    }
}