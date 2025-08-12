
package com.fbreaperv1.model;

import java.util.List;
import java.util.Map;


public class LinkAnalysisResult {
	private String postId;
	private List<Object> nodes;
	private List<Object> edges;
	private Map<String, Object> metrics;

	private List<Map<String, Object>> relationships;

	public LinkAnalysisResult() {}

	public LinkAnalysisResult(String postId, List<Object> nodes, List<Object> edges, Map<String, Object> metrics) {
		this.postId = postId;
		this.nodes = nodes;
		this.edges = edges;
		this.metrics = metrics;
	}

	public String getPostId() { return postId; }
	public void setPostId(String postId) { this.postId = postId; }
	public List<Object> getNodes() { return nodes; }
	public void setNodes(List<Object> nodes) { this.nodes = nodes; }
	public List<Object> getEdges() { return edges; }
	public void setEdges(List<Object> edges) { this.edges = edges; }
	public Map<String, Object> getMetrics() { return metrics; }
	public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }

	public List<Map<String, Object>> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Map<String, Object>> relationships) {
		this.relationships = relationships;
	}

}
