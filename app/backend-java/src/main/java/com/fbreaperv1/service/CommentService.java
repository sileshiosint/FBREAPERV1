package com.fbreaperv1.service;

import com.fbreaperv1.model.Comment;
import com.fbreaperv1.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(String id) {
    return commentRepository.findById(Long.parseLong(id));
    }

    public List<Comment> getAllComments() {
        return (List<Comment>) commentRepository.findAll();
    }

    public void deleteComment(String id) {
    commentRepository.deleteById(Long.parseLong(id));
    }
}
