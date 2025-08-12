package com.fbreaperv1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class HealthController {

	@GetMapping("/api/health")
	public ResponseEntity<Map<String, Object>> healthCheck() {
		Map<String, Object> status = new HashMap<>();
		status.put("status", "UP");
		status.put("timestamp", Instant.now().toString());
		return ResponseEntity.ok(status);
	}
}
