
package com.fbreaperv1.dto;

public class CommentDTO {
    private String id;
    private String postId;
    private String author;
    private String text;
    private String timestamp;
    private String sentiment;

    public CommentDTO() {}

    public CommentDTO(String id, String postId, String author, String text, String timestamp, String sentiment) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
        this.sentiment = sentiment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
}
