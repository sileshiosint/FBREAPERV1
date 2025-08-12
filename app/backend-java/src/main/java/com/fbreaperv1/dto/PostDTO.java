
package com.fbreaperv1.dto;

public class PostDTO {
    private String id;
    private String author;
    private String content;
    private String timestamp;
    private String hashtags;
    private String language;
    private String sentiment;

    public PostDTO() {}

    public PostDTO(String id, String author, String content, String timestamp, String hashtags, String language, String sentiment) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
        this.hashtags = hashtags;
        this.language = language;
        this.sentiment = sentiment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
}
