package com.fbreaperv1.controller;

import com.fbreaperv1.service.LinkAnalysisService;
import com.fbreaperv1.model.LinkAnalysisResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/network")
@CrossOrigin(origins = "http://localhost:3000")
public class LinkAnalysisController {

    private final LinkAnalysisService linkAnalysisService;

    public LinkAnalysisController(LinkAnalysisService linkAnalysisService) {
        this.linkAnalysisService = linkAnalysisService;
    }

    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getNetworkGraph(@RequestParam(required = false) String keyword) {
        // Get real network data from Neo4j
        Map<String, Object> graphData = new HashMap<>();
        
        try {
            // Use LinkAnalysisService to get real data from Neo4j
            Object nodes = linkAnalysisService.getNetworkNodes(keyword);
            Object links = linkAnalysisService.getNetworkLinks(keyword);
            
            graphData.put("nodes", nodes != null ? nodes : new Object[0]);
            graphData.put("links", links != null ? links : new Object[0]);
        } catch (Exception e) {
            // Return empty data if service fails
            graphData.put("nodes", new Object[0]);
            graphData.put("links", new Object[0]);
        }
        
        return ResponseEntity.ok(graphData);
    }

    @GetMapping("/link-analysis")
    public ResponseEntity<LinkAnalysisResult> analyzeLinks(@RequestParam String url) {
        return ResponseEntity.ok(linkAnalysisService.analyzeLinks(url));
    }
}
