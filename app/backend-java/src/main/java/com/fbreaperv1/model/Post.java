package com.fbreaperv1.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Post")
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private String postId;
    private String content;
    private String author;
    private String timestamp;
    private String language;
    private String sentiment;

    @Relationship(type = "HAS_COMMENT", direction = Relationship.Direction.OUTGOING)
    private List<Comment> comments;

    private String postType;
    private String createdTime;
    private java.util.List<String> hashtags;

    public Post() {}
    public String getPostType() {
        return postType;
    }
    public void setPostType(String postType) {
        this.postType = postType;
    }
    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
    public void setHashtags(java.util.List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Post(String postId, String content, String author, String timestamp, String language, String sentiment) {
        this.postId = postId;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
        this.language = language;
        this.sentiment = sentiment;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public List<Comment> getComments() {
        return comments;
    }

            // Added getter for hashtags to fix compilation error
            public List<String> getHashtags() {
                return hashtags;
            }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
