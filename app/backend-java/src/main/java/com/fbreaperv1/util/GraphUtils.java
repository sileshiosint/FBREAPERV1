package com.fbreaperv1.util;

import java.util.List;
import java.util.Map;

public class GraphUtils {
    public static int countNodes(List<Map<String, Object>> nodes) {
        return nodes != null ? nodes.size() : 0;
    }
    public static int countRelationships(List<Map<String, Object>> relationships) {
        return relationships != null ? relationships.size() : 0;
    }

    // Convert LinkAnalysisResult to D3.js format
    public static Map<String, Object> convertToD3Format(com.fbreaperv1.model.LinkAnalysisResult result) {
        return Map.of(
            "nodes", result.getNodes(),
            "links", result.getEdges(),
            "metrics", result.getMetrics()
        );
    }

    // Extract hashtags from text
    public static List<String> extractHashtags(String text) {
        if (text == null) return List.of();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("#(\\w+)");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        java.util.List<String> hashtags = new java.util.ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group(1));
        }
        return hashtags;
    }
}
