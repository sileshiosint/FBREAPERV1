package com.fbreaperv1.kafka;

import com.fbreaperv1.service.DataService;
import com.fbreaperv1.service.ScraperService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class KafkaConsumerService {

    private final DataService dataService;
    private final ScraperService scraperService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(DataService dataService, ScraperService scraperService) {
        this.dataService = dataService;
        this.scraperService = scraperService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "fbreaper-topic", groupId = "fbreaper-group")
    public void consumeMessage(String message) {
        System.out.println("Received Kafka message: " + message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            // Check if it's a post message
            if (jsonNode.has("postId") && jsonNode.has("content")) {
                // Update scraper progress
                scraperService.updateProgress(1, 1); // Increment processed items
                
                // Process the post data
                dataService.processIncomingData(message);
            }
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }
}
