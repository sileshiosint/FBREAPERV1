package com.fbreaperv1.mapper;

import com.fbreaperv1.dto.PostDTO;
import com.fbreaperv1.model.Post;

public class PostMapper {
    public static PostDTO toDTO(Post post) {
        if (post == null) return null;
        String hashtags = post.getHashtags() != null ? String.join(",", post.getHashtags()) : null;
        return new PostDTO(
            post.getId() != null ? post.getId().toString() : null,
            post.getAuthor(),
            post.getContent(),
            post.getTimestamp(),
            hashtags,
            post.getLanguage(),
            post.getSentiment()
        );
    }
    public static Post toEntity(PostDTO dto) {
        if (dto == null) return null;
        Post post = new Post();
        // Setters for all fields
        // ...
        return post;
    }
}
