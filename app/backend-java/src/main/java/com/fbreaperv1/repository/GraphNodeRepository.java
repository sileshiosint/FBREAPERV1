package com.fbreaperv1.repository;

import com.fbreaperv1.model.GraphNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphNodeRepository extends Neo4jRepository<GraphNode, String> {
}
