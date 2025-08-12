package com.fbreaperv1.service;

import com.fbreaperv1.model.Post;
import com.fbreaperv1.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> getPostById(String id) {
    return postRepository.findById(Long.parseLong(id));
    }

    public List<Post> getAllPosts() {
        return (List<Post>) postRepository.findAll();
    }

    public void deletePost(String id) {
    postRepository.deleteById(Long.parseLong(id));
    }
}
