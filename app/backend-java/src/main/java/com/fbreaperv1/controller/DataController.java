package com.fbreaperv1.controller;

import com.fbreaperv1.kafka.KafkaProducerService;
import com.fbreaperv1.model.KafkaMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "http://localhost:3000")
public class DataController {
    private final KafkaProducerService kafkaProducerService;
    private final com.fbreaperv1.service.DataService dataService;
    private final com.fbreaperv1.common.EntityMapper entityMapper;

    public DataController(KafkaProducerService kafkaProducerService, com.fbreaperv1.service.DataService dataService, com.fbreaperv1.common.EntityMapper entityMapper) {
        this.kafkaProducerService = kafkaProducerService;
        this.dataService = dataService;
        this.entityMapper = entityMapper;
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page, 
                                        @RequestParam(defaultValue = "20") int size) {
        var posts = dataService.getAllPosts();
        // Simple pagination
        int start = page * size;
        int end = Math.min(start + size, posts.size());
        var paginatedPosts = posts.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts.stream().map(entityMapper::toPostDTO).toList());
        response.put("totalElements", posts.size());
        response.put("totalPages", (int) Math.ceil((double) posts.size() / size));
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getAllComments() {
        return ResponseEntity.ok(dataService.getAllComments().stream().map(entityMapper::toCommentDTO).toList());
    }

    @GetMapping("/posts/search")
    public ResponseEntity<?> searchPostsByKeyword(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        var posts = dataService.searchPostsByKeyword(keyword);
        // Simple pagination
        int start = page * size;
        int end = Math.min(start + size, posts.size());
        var paginatedPosts = posts.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedPosts.stream().map(entityMapper::toPostDTO).toList());
        response.put("totalElements", posts.size());
        response.put("totalPages", (int) Math.ceil((double) posts.size() / size));
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable String postId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        // For now, return all comments since we don't have post-specific filtering
        var comments = dataService.getAllComments();
        // Simple pagination
        int start = page * size;
        int end = Math.min(start + size, comments.size());
        var paginatedComments = comments.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedComments.stream().map(entityMapper::toCommentDTO).toList());
        response.put("totalElements", comments.size());
        response.put("totalPages", (int) Math.ceil((double) comments.size() / size));
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        // Real stats: post count, comment count, and latest post/comment timestamp
        int postCount = dataService.getAllPosts().size();
        int commentCount = dataService.getAllComments().size();
        String latestPostTime = dataService.getAllPosts().stream()
            .map(p -> {
                try { return p.getTimestamp(); } catch (Exception e) { return null; }
            })
            .filter(java.util.Objects::nonNull)
            .max(String::compareTo)
            .orElse(null);
        String latestCommentTime = dataService.getAllComments().stream()
            .map(c -> {
                try { return c.getTimestamp(); } catch (Exception e) { return null; }
            })
            .filter(java.util.Objects::nonNull)
            .max(String::compareTo)
            .orElse(null);
        
        // Calculate total reactions (mock data for now)
        int totalReactions = postCount * 15 + commentCount * 3; // Mock calculation
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts", postCount);
        stats.put("totalComments", commentCount);
        stats.put("totalReactions", totalReactions);
        stats.put("activeScrapers", 1); // Mock data
        stats.put("errorsToday", 0); // Mock data
        stats.put("dataCollectedToday", postCount + commentCount); // Mock data
        
        return ResponseEntity.ok(stats);
    }


    /**
     * Receives scraped data from scraper and sends it to Kafka
     * @param message KafkaMessage object containing type and payload
     * @return HTTP 200 if successful
     */
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestData(@RequestBody KafkaMessage message) {
        if (message.getType() == null || message.getPayload() == null) {
            return ResponseEntity.badRequest().body("Invalid message: type and payload required");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaProducerService.sendMessage(message.getType(), jsonMessage);
            return ResponseEntity.ok("Data sent to Kafka successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to serialize message");
        }
    }

    /**
     * Simple health check for DataController
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("DataController is live", HttpStatus.OK);
    }
}
