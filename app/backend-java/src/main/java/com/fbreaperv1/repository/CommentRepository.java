package com.fbreaperv1.repository;

import com.fbreaperv1.model.Comment;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends Neo4jRepository<Comment, Long> {
	Iterable<Comment> findByTimestampBetween(String from, String to);
	java.util.List<Comment> findByContentContainingIgnoreCase(String keyword);
}
