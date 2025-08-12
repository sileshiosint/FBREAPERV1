
package com.fbreaperv1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CustomQueryRepositoryImpl implements CustomQueryRepository {

    @Autowired
    private Neo4jClient neo4jClient;

    @Override
    public List<Map<String, Object>> getAllNodes(int limit) {
        String query = "MATCH (n) RETURN n LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(query)
                .bind(limit).to("limit")
                .fetch()
                .all());
    }

    @Override
    public List<Map<String, Object>> getAllRelationships(int limit) {
        String query = "MATCH (n)-[r]->(m) RETURN n, r, m LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(query)
                .bind(limit).to("limit")
                .fetch()
                .all());
    }

    // Shortest path query
    public List<Map<String, Object>> findShortestPath(String nodeA, String nodeB) {
        String query = "MATCH (a {id: $nodeA}), (b {id: $nodeB}), p = shortestPath((a)-[*..15]-(b)) RETURN p";
        return new ArrayList<>(neo4jClient.query(query)
                .bind(nodeA).to("nodeA")
                .bind(nodeB).to("nodeB")
                .fetch().all());
    }

    // Community detection (Louvain)
    public List<Map<String, Object>> detectCommunities() {
        String query = "CALL gds.louvain.stream({nodeProjection: 'Node', relationshipProjection: 'REL'}) YIELD nodeId, communityId RETURN nodeId, communityId";
        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }

    // Hashtag co-occurrence
    public List<Map<String, Object>> findHashtagCooccurrence() {
        String query = "MATCH (p:Post)-[:HAS_HASHTAG]->(h:Hashtag) RETURN h.name, count(*) as count ORDER BY count DESC";
        return new ArrayList<>(neo4jClient.query(query).fetch().all());
    }
}
