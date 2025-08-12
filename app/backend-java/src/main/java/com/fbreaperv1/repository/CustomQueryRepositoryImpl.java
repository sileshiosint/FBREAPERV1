
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

    @Override
    public List<Map<String, Object>> getNetworkNodes(String keyword) {
        String query = """
            MATCH (p:Post)
            WHERE $keyword IS NULL OR toLower(p.content) CONTAINS toLower($keyword)
            OPTIONAL MATCH (p)-[:HAS_HASHTAG]->(h:Hashtag)
            OPTIONAL MATCH (p)-[:HAS_COMMENT]->(c:Comment)
            RETURN collect(DISTINCT { id: id(p), label: 'Post', content: p.content, author: p.author }) +
                   collect(DISTINCT { id: id(h), label: 'Hashtag', name: h.name }) +
                   collect(DISTINCT { id: id(c), label: 'Comment', content: c.content, author: c.author }) AS nodes
        """;
        return new ArrayList<>(neo4jClient.query(query)
                .bind(keyword).to("keyword")
                .fetch().all());
    }

    @Override
    public List<Map<String, Object>> getNetworkLinks(String keyword) {
        String query = """
            MATCH (p:Post)
            WHERE $keyword IS NULL OR toLower(p.content) CONTAINS toLower($keyword)
            OPTIONAL MATCH (p)-[r1:HAS_HASHTAG]->(h:Hashtag)
            OPTIONAL MATCH (p)-[r2:HAS_COMMENT]->(c:Comment)
            RETURN collect(DISTINCT { source: id(p), target: id(h), type: type(r1) }) +
                   collect(DISTINCT { source: id(p), target: id(c), type: type(r2) }) AS links
        """;
        return new ArrayList<>(neo4jClient.query(query)
                .bind(keyword).to("keyword")
                .fetch().all());
    }

    @Override
    public List<Map<String, Object>> findPostNeighborhood(String postId) {
        String query = """
            MATCH (p:Post {postId: $postId})
            OPTIONAL MATCH (p)-[:HAS_COMMENT]->(c:Comment)
            OPTIONAL MATCH (p)-[:HAS_HASHTAG]->(h:Hashtag)
            RETURN p as post, collect(DISTINCT c) as comments, collect(DISTINCT h) as hashtags
        """;
        return new ArrayList<>(neo4jClient.query(query)
                .bind(postId).to("postId")
                .fetch().all());
    }
}
