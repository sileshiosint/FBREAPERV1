package com.fbreaperv1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScraperService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String SCRAPER_CONTROL_TOPIC = "scraper-control";
    private static final String SCRAPER_STATUS_URL = "http://localhost:5000/status";
    
    private boolean isActive = false;
    private String currentTarget = "";
    private int progress = 0;
    private int totalItems = 0;
    private int processedItems = 0;
    private LocalDateTime startTime = null;

    public void triggerScraper() {
        kafkaTemplate.send(SCRAPER_CONTROL_TOPIC, "{\"action\":\"start\"}");
        this.isActive = true;
        this.startTime = LocalDateTime.now();
    }

    public void triggerScraperByKeyword(String keyword) {
        String message = String.format("{\"action\":\"scrapeByKeyword\",\"keyword\":\"%s\"}", keyword);
        kafkaTemplate.send(SCRAPER_CONTROL_TOPIC, message);
        this.isActive = true;
        this.currentTarget = keyword;
        this.startTime = LocalDateTime.now();
        this.progress = 0;
        this.processedItems = 0;
    }

    public Map<String, Object> getCurrentStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isActive", isActive);
        status.put("currentTarget", currentTarget);
        status.put("progress", progress);
        status.put("totalItems", totalItems);
        status.put("processedItems", processedItems);
        status.put("startTime", startTime != null ? startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        status.put("estimatedCompletion", calculateEstimatedCompletion());
        
        // Mock errors for now
        Map<String, Object>[] errors = new Map[0];
        status.put("errors", errors);
        
        return status;
    }

    private String calculateEstimatedCompletion() {
        if (!isActive || startTime == null || processedItems == 0) {
            return "Unknown";
        }
        
        long elapsedMinutes = java.time.Duration.between(startTime, LocalDateTime.now()).toMinutes();
        if (elapsedMinutes == 0) return "Unknown";
        
        double itemsPerMinute = (double) processedItems / elapsedMinutes;
        if (itemsPerMinute == 0) return "Unknown";
        
        int remainingItems = totalItems - processedItems;
        int remainingMinutes = (int) (remainingItems / itemsPerMinute);
        
        if (remainingMinutes < 60) {
            return remainingMinutes + "m";
        } else {
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            return hours + "h " + minutes + "m";
        }
    }

    public void updateProgress(int processed, int total) {
        this.processedItems = processed;
        this.totalItems = total;
        this.progress = total > 0 ? (processed * 100) / total : 0;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
