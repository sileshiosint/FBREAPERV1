package com.fbreaperv1.repository;

import com.fbreaperv1.model.GraphRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphRelationshipRepository extends Neo4jRepository<GraphRelationship, Long> {
	// Custom CRUD methods
	void deleteById(Long id);
	Iterable<GraphRelationship> findByType(String type);
	void deleteByType(String type);
}
