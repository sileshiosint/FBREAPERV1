package com.fbreaperv1.mapper;

import com.fbreaperv1.dto.CommentDTO;
import com.fbreaperv1.model.Comment;

public class CommentMapper {
    public static CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;
        return new CommentDTO(
            comment.getId() != null ? comment.getId().toString() : null,
            comment.getCommentId(),
            comment.getAuthor(),
            comment.getContent(),
            comment.getTimestamp(),
            comment.getSentiment()
        );
    }
    public static Comment toEntity(CommentDTO dto) {
        if (dto == null) return null;
        Comment comment = new Comment();
        // Setters for all fields
        // ...
        return comment;
    }
}
