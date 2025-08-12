# FBReaperV1 Backend Java Code Skeleton

This document lists every Java code file in the backend, grouped by package, with a one-line summary for each file. Use this as a reference for the backend structure and responsibilities.

---

## Application Starters
- **FBReaperV1Application.java**: Main Spring Boot application entry point.
- **FBReaperBackendApplication.java**: (If present) Backend-specific application starter.
- **Application.java**: (If present) Placeholder or legacy entry point.

## common
- **EntityMapper.java**: Maps between entities and DTOs.
- **GlobalExceptionHandler.java**: Handles exceptions globally for REST APIs.
- **GraphUtils.java**: Utility for graph operations and text processing.

## config
- **KafkaConfig.java**: Configures Kafka beans and listeners.
- **KafkaTopicConfig.java**: Defines Kafka topics.
- **Neo4jConfig.java**: Configures Neo4j driver bean.
- **SecurityConfig.java**: Configures security (CSRF, etc).

## controller
- **CommentController.java**: REST API for comment CRUD.
- **DataController.java**: Data import/export and statistics.
- **HealthController.java**: Health check endpoint.
- **LinkAnalysisController.java**: Link analysis endpoint.
- **PostController.java**: REST API for post CRUD.
- **ScraperController.java**: Triggers/manages scraping tasks via Kafka.

## dto
- **CommentDTO.java**: DTO for comments.
- **LinkAnalysisResultDTO.java**: DTO for link analysis results.
- **PostDTO.java**: DTO for posts.

## exception
- **PostNotFoundException.java**: Exception for missing posts.
- **ScraperException.java**: Exception for scraper errors.

## kafka
- **KafkaConsumerService.java**: Consumes messages from Kafka.
- **KafkaProducerService.java**: Produces messages to Kafka.

## mapper
- **CommentMapper.java**: Maps between Comment and CommentDTO.
- **LinkAnalysisMapper.java**: Maps between LinkAnalysisResult and LinkAnalysisResultDTO.
- **PostMapper.java**: Maps between Post and PostDTO.

## model
- **Comment.java**: Neo4j node for comments.
- **GraphNode.java**: Neo4j node for graph nodes.
- **GraphRelationship.java**: Relationship entity.
- **KafkaMessage.java**: Kafka message model.
- **LinkAnalysisResult.java**: Link analysis result model.
- **Post.java**: Neo4j node for posts.

## repository
- **CommentRepository.java**: Neo4j repository for comments.
- **CustomQueryRepository.java**: Custom graph queries interface.
- **CustomQueryRepositoryImpl.java**: Implementation of custom graph queries.
- **GraphNodeRepository.java**: Neo4j repository for graph nodes.
- **GraphRelationshipRepository.java**: Neo4j repository for graph relationships.
- **PostRepository.java**: Neo4j repository for posts.
- **LinkAnalysisResult.java**: (If present) Model or repository for link analysis results.

## service
- **CommentService.java**: Business logic for comments.
- **DataService.java**: Data import/export, search, and filter for posts/comments.
- **LinkAnalysisService.java**: Link analysis, shortest path, and community detection.
- **PostService.java**: Business logic for posts.
- **ScraperService.java**: Manages scraping tasks.

## util
- **GraphUtils.java**: Utility for graph operations and text processing.

---

This skeleton covers every Java file in the backend. For more details, see the code or the existing README_BACKEND_SERVICES.md.
