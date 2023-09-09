package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .id(comment.getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .item(comment.getItem())
                .build();
    }

    public static Comment toComment(CommentDto comment) {
        return Comment.builder()
                .text(comment.getText())
                .id(comment.getId())
                .created(comment.getCreated())
                .item(comment.getItem())
                .build();
    }

    public static List<CommentDto> toCommentDto(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toCommentDto(comment));
        }

        return result;
    }
}
