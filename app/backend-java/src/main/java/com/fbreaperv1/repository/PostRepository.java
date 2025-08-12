package com.fbreaperv1.repository;

import com.fbreaperv1.model.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends Neo4jRepository<Post, Long> {
    Post findByPostId(String postId);
    Iterable<Post> findByTimestampBetween(String from, String to);
    java.util.List<Post> findByContentContainingIgnoreCase(String keyword);
}
