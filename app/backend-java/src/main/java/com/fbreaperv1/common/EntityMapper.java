package com.fbreaperv1.common;

import com.fbreaperv1.dto.PostDTO;
import com.fbreaperv1.model.Post;
import com.fbreaperv1.dto.CommentDTO;
import com.fbreaperv1.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public PostDTO toPostDTO(Post post) {
        return new PostDTO(
            post.getId() != null ? post.getId().toString() : null,
            post.getAuthor(),
            post.getContent(),
            post.getTimestamp(),
            post.getHashtags() != null ? String.join(",", post.getHashtags()) : null,
            post.getLanguage(),
            post.getSentiment()
        );
    }
    public CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
            comment.getId() != null ? comment.getId().toString() : null,
            comment.getPostId(),
            comment.getAuthor(),
            comment.getContent(),
            comment.getTimestamp(),
            comment.getSentiment()
        );
    }
}
