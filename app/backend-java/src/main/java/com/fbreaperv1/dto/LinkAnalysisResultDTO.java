
package com.fbreaperv1.dto;

import java.util.List;
import java.util.Map;

public class LinkAnalysisResultDTO {
    private List<Object> nodes;
    private List<Object> edges;
    private Map<String, Object> metrics;

    public LinkAnalysisResultDTO() {}

    public LinkAnalysisResultDTO(List<Object> nodes, List<Object> edges, Map<String, Object> metrics) {
        this.nodes = nodes;
        this.edges = edges;
        this.metrics = metrics;
    }

    public List<Object> getNodes() { return nodes; }
    public void setNodes(List<Object> nodes) { this.nodes = nodes; }
    public List<Object> getEdges() { return edges; }
    public void setEdges(List<Object> edges) { this.edges = edges; }
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
}
