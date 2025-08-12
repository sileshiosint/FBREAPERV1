package com.fbreaperv1.controller;

import com.fbreaperv1.kafka.KafkaProducerService;
import com.fbreaperv1.service.ScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
@CrossOrigin(origins = "http://localhost:3000")
public class ScraperController {

	private final KafkaProducerService kafkaProducerService;
	private final ScraperService scraperService;

	public ScraperController(KafkaProducerService kafkaProducerService, ScraperService scraperService) {
		this.kafkaProducerService = kafkaProducerService;
		this.scraperService = scraperService;
	}

	/**
	 * Triggers the scraper to start scraping (all or default)
	 */
	@PostMapping("/start")
	public ResponseEntity<String> startScraper() {
		kafkaProducerService.sendMessage("scraper-control", "{\"action\":\"start\"}");
		return ResponseEntity.ok("Scraper start command sent");
	}

	/**
	 * Triggers the scraper to scrape by keyword or username
	 */
	@PostMapping("/scrapeByKeyword")
	public ResponseEntity<String> scrapeByKeyword(@RequestParam String keyword) {
		String msg = String.format("{\"action\":\"scrapeByKeyword\",\"keyword\":\"%s\"}", keyword);
		kafkaProducerService.sendMessage("scraper-control", msg);
		return ResponseEntity.ok("Scrape by keyword command sent");
	}

	/**
	 * Get current scraper status
	 */
	@GetMapping("/status")
	public ResponseEntity<Map<String, Object>> getScraperStatus() {
		Map<String, Object> status = scraperService.getCurrentStatus();
		return ResponseEntity.ok(status);
	}
}
