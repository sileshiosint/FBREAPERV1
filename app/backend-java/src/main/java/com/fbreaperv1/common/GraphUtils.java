package com.fbreaperv1.common;

import java.util.List;
import java.util.Map;

public class GraphUtils {
    public static int countNodes(List<Map<String, Object>> nodes) {
        return nodes != null ? nodes.size() : 0;
    }
    public static int countRelationships(List<Map<String, Object>> relationships) {
        return relationships != null ? relationships.size() : 0;
    }
}
