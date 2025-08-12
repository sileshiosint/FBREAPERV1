

# FBReaperV1 Backend Java Services

This document provides a clear, concise overview of every Java code file in the FBReaperV1 backend. Each file is described with its main responsibilities and implemented methods only.

---

## 1. `common`
- **EntityMapper.java**: Maps between entity objects (`Post`, `Comment`) and their DTOs (`PostDTO`, `CommentDTO`).
	- `toPostDTO(Post post)`: Converts a `Post` entity to a `PostDTO`.
	- `toCommentDTO(Comment comment)`: Converts a `Comment` entity to a `CommentDTO`.
- **GlobalExceptionHandler.java**: Handles exceptions globally for REST APIs using `@ControllerAdvice`.
	- Handles all exceptions and validation errors, returning structured error responses with HTTP status codes.
- **GraphUtils.java**: Utility class for graph operations and text processing.
	- `countNodes(List<Map<String, Object>> nodes)`: Returns the number of nodes.
	- `countRelationships(List<Map<String, Object>> relationships)`: Returns the number of relationships.
	- `convertToD3Format(LinkAnalysisResult result)`: Converts a link analysis result to D3.js format.
	- `extractHashtags(String text)`: Extracts hashtags from a given text.

## 2. `config`
- **KafkaConfig.java**: Configures Kafka producer/consumer beans, listener factory, and a default topic.
- **KafkaTopicConfig.java**: Defines Kafka topics: `fb-posts`, `fb-comments`, `fb-link-analysis` (3 partitions, 1 replica each).
- **Neo4jConfig.java**: Configures the Neo4j `Driver` bean.
- **SecurityConfig.java**: Disables CSRF and allows all requests (default, can be changed for real authentication).

## 3. `controller`
- **CommentController.java**: REST API for comment CRUD. Uses `CommentService`.
- **DataController.java**: Data import/export and statistics. Returns DTOs and stats.
- **HealthController.java**: Health check endpoint (`/api/health`).
- **LinkAnalysisController.java**: Link analysis endpoint (`/api/link-analysis/{postId}`).
- **PostController.java**: REST API for post CRUD. Uses `PostService`.
- **ScraperController.java**: Triggers/manages scraping tasks via Kafka.

## 4. `dto`
- **CommentDTO.java**: DTO for comments (id, postId, author, text, timestamp, sentiment).
- **LinkAnalysisResultDTO.java**: DTO for link analysis results (nodes, edges, metrics).
- **PostDTO.java**: DTO for posts (id, author, content, timestamp, hashtags, language, sentiment).

## 5. `exception`
- **PostNotFoundException.java**: Exception for missing posts.
- **ScraperException.java**: Exception for scraper errors.

## 6. `kafka`
- **KafkaConsumerService.java**: Consumes messages from Kafka (`fbreaper-topic`), passes to `DataService`.
- **KafkaProducerService.java**: Produces messages to Kafka topics (`sendMessage`).

## 7. `mapper`
- **CommentMapper.java**: Maps between `Comment` and `CommentDTO`.
- **LinkAnalysisMapper.java**: Maps between `LinkAnalysisResult` and `LinkAnalysisResultDTO`.
- **PostMapper.java**: Maps between `Post` and `PostDTO`.

## 8. `model`
- **Comment.java**: Neo4j node for comments (id, commentId, content, author, timestamp, language, sentiment, createdTime).
- **GraphNode.java**: Neo4j node for graph nodes (id, label, type).
- **GraphRelationship.java**: Relationship entity (id, target, type).
- **KafkaMessage.java**: Kafka message model (type, payload).
- **LinkAnalysisResult.java**: Link analysis result model (postId, nodes, edges, metrics, relationships).
- **Post.java**: Neo4j node for posts (id, postId, content, author, timestamp, language, sentiment, comments, postType, createdTime, hashtags).

## 9. `repository`
- **CommentRepository.java**: Neo4j repository for comments (find by timestamp, content).
- **CustomQueryRepository.java / Impl.java**: Custom graph queries (all nodes, relationships, shortest path, communities, hashtag co-occurrence).
- **GraphNodeRepository.java**: Neo4j repository for graph nodes.
- **GraphRelationshipRepository.java**: Neo4j repository for graph relationships (delete/find by type).
- **PostRepository.java**: Neo4j repository for posts (find by postId, timestamp, content).

## 10. `service`
- **CommentService.java**: Business logic for comments (save, get by ID, list, delete).
- **DataService.java**: Data import/export, search, and filter for posts/comments.
- **LinkAnalysisService.java**: Link analysis, shortest path, and community detection.
- **PostService.java**: Business logic for posts (save, get by ID, list, delete).
- **ScraperService.java**: Manages scraping tasks (trigger via Kafka, get status from Python service).

## 11. `util`
- **GraphUtils.java**: Utility for graph operations and text processing (see `common`).

## Main Application Classes
- **Application.java**: (Empty/placeholder).
- **FBReaperV1Application.java**: Main Spring Boot application starter.
- **FBReaperBackendApplication.java**: Backend-specific application starter (if present).

---

This structure follows standard Spring Boot practices, with clear separation of concerns for configuration, controllers (REST APIs), services (business logic), repositories (data access), and models (entities/DTOs).
