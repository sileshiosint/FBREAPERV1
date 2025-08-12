package com.fbreaperv1.service;

import com.fbreaperv1.model.LinkAnalysisResult;
import com.fbreaperv1.repository.CustomQueryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LinkAnalysisService {

    private final CustomQueryRepository customQueryRepository;

    public LinkAnalysisService(CustomQueryRepository customQueryRepository) {
        this.customQueryRepository = customQueryRepository;
    }

    public LinkAnalysisResult analyzeLinks(String postId) {
        // Build a small ego-network around a specific postId from Neo4j
        if (customQueryRepository instanceof com.fbreaperv1.repository.CustomQueryRepositoryImpl impl) {
            List<Map<String, Object>> records = impl.findPostNeighborhood(postId);
            LinkAnalysisResult result = new LinkAnalysisResult();
            result.setPostId(postId);

            if (!records.isEmpty()) {
                Map<String, Object> row = records.get(0);
                Object post = row.get("post");
                Object comments = row.get("comments");
                Object hashtags = row.get("hashtags");

                // Pack nodes and edges into simple structures expected by the frontend
                // Nodes
                java.util.List<Object> nodes = new java.util.ArrayList<>();
                if (post != null) nodes.add(post);
                if (comments instanceof List<?> list) nodes.addAll(list);
                if (hashtags instanceof List<?> list) nodes.addAll(list);

                // Edges (basic summaries)
                java.util.List<Object> edges = new java.util.ArrayList<>();
                if (comments instanceof List<?> list) {
                    for (Object c : list) {
                        Map<String, Object> e = new HashMap<>();
                        e.put("type", "HAS_COMMENT");
                        e.put("source", "post");
                        e.put("target", c);
                        edges.add(e);
                    }
                }
                if (hashtags instanceof List<?> list) {
                    for (Object h : list) {
                        Map<String, Object> e = new HashMap<>();
                        e.put("type", "HAS_HASHTAG");
                        e.put("source", "post");
                        e.put("target", h);
                        edges.add(e);
                    }
                }

                result.setNodes(nodes);
                result.setEdges(edges);

                Map<String, Object> metrics = new HashMap<>();
                metrics.put("numComments", comments instanceof List<?> l ? l.size() : 0);
                metrics.put("numHashtags", hashtags instanceof List<?> l2 ? l2.size() : 0);
                result.setMetrics(metrics);
            }
            return result;
        }
        return new LinkAnalysisResult(postId, java.util.Collections.emptyList(), java.util.Collections.emptyList(), java.util.Collections.emptyMap());
    }


    public Object calculateShortestPath(String nodeA, String nodeB) {
        // Returns the shortest path as a list of nodes/relationships
        return customQueryRepository instanceof com.fbreaperv1.repository.CustomQueryRepositoryImpl
            ? ((com.fbreaperv1.repository.CustomQueryRepositoryImpl) customQueryRepository).findShortestPath(nodeA, nodeB)
            : null;
    }

    public Object detectCommunities() {
        // Returns community detection results
        return customQueryRepository instanceof com.fbreaperv1.repository.CustomQueryRepositoryImpl
            ? ((com.fbreaperv1.repository.CustomQueryRepositoryImpl) customQueryRepository).detectCommunities()
            : null;
    }

    public Object getNetworkNodes(String keyword) {
        if (customQueryRepository instanceof com.fbreaperv1.repository.CustomQueryRepositoryImpl impl) {
            List<Map<String, Object>> rows = impl.getNetworkNodes(keyword);
            if (!rows.isEmpty()) {
                return rows.get(0).getOrDefault("nodes", java.util.Collections.emptyList());
            }
            return java.util.Collections.emptyList();
        }
        return java.util.Collections.emptyList();
    }

    public Object getNetworkLinks(String keyword) {
        if (customQueryRepository instanceof com.fbreaperv1.repository.CustomQueryRepositoryImpl impl) {
            List<Map<String, Object>> rows = impl.getNetworkLinks(keyword);
            if (!rows.isEmpty()) {
                return rows.get(0).getOrDefault("links", java.util.Collections.emptyList());
            }
            return java.util.Collections.emptyList();
        }
        return java.util.Collections.emptyList();
    }
}
