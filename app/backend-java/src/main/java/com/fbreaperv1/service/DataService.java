
package com.fbreaperv1.service;

import com.fbreaperv1.model.Post;
import com.fbreaperv1.model.Comment;
import com.fbreaperv1.repository.PostRepository;
import com.fbreaperv1.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public DataService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }


    public List<Post> searchPostsByKeyword(String keyword) {
        // Example: assumes PostRepository has a method findByContentContainingIgnoreCase
        return postRepository.findByContentContainingIgnoreCase(keyword);
    }

    public List<Comment> searchCommentsByKeyword(String keyword) {
    // Search comments by keyword in content
    return commentRepository.findByContentContainingIgnoreCase(keyword);
    }

    public List<Post> filterPostsByDate(String fromDate, String toDate) {
        Iterable<Post> posts = postRepository.findByTimestampBetween(fromDate, toDate);
        List<Post> result = new java.util.ArrayList<>();
        posts.forEach(result::add);
        return result;
    }

    public List<Comment> filterCommentsByDate(String fromDate, String toDate) {
        Iterable<Comment> comments = commentRepository.findByTimestampBetween(fromDate, toDate);
        List<Comment> result = new java.util.ArrayList<>();
        comments.forEach(result::add);
        return result;
    }
    public void processIncomingData(String message) {
        // Example processing: log, parse, and save if JSON matches Post or Comment
        System.out.println("Processing incoming data: " + message);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(message);
            
            // Check if it's a post (has postId and content)
            if (node.has("postId") && node.has("content")) {
                Post post = new Post();
                post.setPostId(node.get("postId").asText());
                post.setContent(node.get("content").asText());
                post.setAuthor(node.get("author").asText());
                post.setTimestamp(node.get("timestamp").asText());
                post.setLanguage(node.get("language").asText());
                post.setSentiment(node.get("sentiment").asText());
                post.setPostType(node.get("postType").asText());
                post.setCreatedTime(node.get("createdTime").asText());
                
                // Handle hashtags array
                if (node.has("hashtags") && node.get("hashtags").isArray()) {
                    java.util.List<String> hashtags = new java.util.ArrayList<>();
                    for (com.fasterxml.jackson.databind.JsonNode hashtag : node.get("hashtags")) {
                        hashtags.add(hashtag.asText());
                    }
                    post.setHashtags(hashtags);
                }
                
                savePost(post);
                System.out.println("Saved post: " + post.getPostId());
            } else if (node.has("content") && node.has("author")) {
                // Assume it's a comment
                Comment comment = mapper.treeToValue(node, Comment.class);
                saveComment(comment);
                System.out.println("Saved comment: " + comment.getId());
            }
        } catch (Exception e) {
            System.err.println("Failed to process incoming data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
