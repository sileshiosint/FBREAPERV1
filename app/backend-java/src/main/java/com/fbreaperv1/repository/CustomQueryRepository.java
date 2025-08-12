package com.fbreaperv1.repository;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomQueryRepository {

    @Query("MATCH (n) RETURN n LIMIT $limit")
    List<Map<String, Object>> getAllNodes(int limit);

    @Query("MATCH (n)-[r]->(m) RETURN n, r, m LIMIT $limit")
    List<Map<String, Object>> getAllRelationships(int limit);
}
