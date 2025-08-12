# Facebook OSINT Dashboard (Production-Ready Monorepo)

A full-stack, containerized OSINT system for Facebook data collection and analysis. It comprises a React (Vite) frontend, a Java Spring Boot backend, and a Python Playwright scraper microservice, with Kafka for streaming and Neo4j as the graph database.

## Architecture

```
┌──────────────────┐     ┌──────────────────────┐      ┌────────────────────┐
│  Frontend (Nginx)│ ◄──►│  Backend (SpringBoot)│ ◄──► │  Scraper (Python)  │
│  :3000            │     │  :8080               │      │  Kafka Producer    │
└──────────────────┘     └──────────────────────┘      └────────────────────┘
          ▲                         ▲                              ▲
          │                         │                              │
          │                 ┌───────┴────────┐                     │
          │                 │   Kafka :9092  │◄────────────────────┘
          │                 └────────────────┘
          │
          │                 ┌────────────────┐
          └────────────────►│  Neo4j :7687   │
                            └────────────────┘
```

- Topics used:
  - `fbreaper-topic`: scraper → backend (posts/messages)
  - `scraper-control`: backend → scraper (commands)

## What's Included

- Docker Compose for Neo4j, Kafka, Backend, Scraper, Frontend
- New frontend rebuilt from scratch using Vite + React + Tailwind
- Spring Boot backend wired to Kafka and Neo4j with REST APIs
- Python scraper using Playwright + NLP that streams to Kafka
- Unified `.env.example` files and production Dockerfiles
- Start/stop scripts for local dev convenience

## Quick Start (Docker)

1) Prerequisites
- Docker 20+, Docker Compose v2

2) Start the full stack
```bash
cd app
docker compose up -d --build
```

3) Access services
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080/api/health`
- Neo4j Browser: `http://localhost:7474` (neo4j/neo4jpassword)

4) Trigger scraping
- From frontend UI (Scraper Controls)
- Or via API:
```bash
curl -X POST "http://localhost:8080/api/scraper/scrapeByKeyword?keyword=osint"
```

5) View data
- Frontend Posts table (paginated)
- Backend APIs under `/api/data/*`

## Local Development (without Docker)

- Infra: `docker compose up -d neo4j kafka`
- Backend:
```bash
cd backend-java
mvn spring-boot:run
```
- Scraper:
```bash
cd python_scraper
python3 -m venv venv && source venv/bin/activate
pip install -r requirements.txt
python -m playwright install chromium
python -m spacy download en_core_web_sm
cp .env.example .env  # adjust if needed
python main.py
```
- Frontend:
```bash
cd frontend
npm install
npm run dev
```

## Configuration

- Backend `application.properties` defaults to local ports. When running in Docker, env vars from compose override hostnames (Neo4j `neo4j`, Kafka `kafka`).
- Frontend: `frontend/.env.example`
```
VITE_API_BASE_URL=http://localhost:8080/api
```
- Scraper: `python_scraper/.env.example`
```
KAFKA_BROKER=localhost:9092
KAFKA_SCRAPE_TOPIC=fbreaper-topic
KAFKA_COMMANDS_TOPIC=scraper-control
LIBRETRANSLATE_URL=http://localhost:5001/translate
```
- Playwright persistent session directory: `python_scraper/fb_session` (create and sign in once manually if needed)

## REST API (Backend)

- Scraper
  - `POST /api/scraper/start`
  - `POST /api/scraper/scrapeByKeyword?keyword={keyword}`
  - `GET /api/scraper/status`
- Data
  - `GET /api/data/posts?page=0&size=20`
  - `GET /api/data/posts/search?keyword={k}&page=0&size=20`
  - `GET /api/data/posts/{postId}/comments?page=0&size=20`
  - `GET /api/data/stats`
- Network
  - `GET /api/network/graph?keyword={k}`
  - `GET /api/network/link-analysis?url={url}`
- Health
  - `GET /api/health`

## Production Notes

- Frontend served via Nginx with `/api` reverse proxy to backend service
- Java image is built via multi-stage Maven, runs on Temurin JRE 17
- Scraper image uses Playwright base for headless Chromium
- Kafka topics auto-created by backend (`fbreaper-topic`, `scraper-control`)
- CORS enabled for `http://localhost:3000` by default

## Operations

- Start all (dev): `./start-system.sh`
- Stop all (dev): `./stop-system.sh`
- Compose: `docker compose up -d --build` / `docker compose down`

## Troubleshooting

- Kafka connectivity: ensure `kafka:9092` in Docker, `localhost:9092` locally
- Neo4j auth: use `neo4j/neo4jpassword`
- Playwright: make sure Chromium is installed and session exists; try non-headless for debugging
- Logs:
  - Backend: container logs or `backend.log`
  - Scraper: container logs or `scraper.log`
  - Frontend: container logs or `frontend.log`

## Security and Compliance

- Do not scrape without complying with Facebook TOS and local laws
- Add authentication/authorization and rate-limiting before production
- Store secrets via environment variables or secret managers

## Folder Structure

```
app/
├─ backend-java/
│  ├─ src/main/java/com/fbreaperv1/...
│  ├─ src/main/resources/application.properties
│  └─ Dockerfile
├─ python_scraper/
│  ├─ scraper/, nlp/, kafka_client/
│  ├─ .env.example
│  └─ Dockerfile
├─ frontend/
│  ├─ src/
│  ├─ index.html
│  ├─ package.json
│  └─ Dockerfile
├─ docker-compose.yml
├─ start-system.sh
└─ stop-system.sh
```

## License

Educational and research use only.

## Deep Dive: How It Works End-to-End

This section explains the exact launch flow, processing path, data graph model, and details of every important source file in the Java backend and Python scraper.

### Launch Flow (Docker)

1) `docker compose up -d --build`
- Builds `backend-java`, `python_scraper`, and `frontend` images, starts Neo4j, Zookeeper, Kafka, Backend, Scraper, Frontend in an isolated network.
- Kafka advertised listeners are configured so containers can talk internally (`kafka:9092`) and you can connect locally (`localhost:29092` if needed).

2) Backend starts
- Spring Boot runs with `application.properties`, discovers `@Configuration` and `@KafkaListener` beans, creates Kafka topics (`fbreaper-topic`, `scraper-control`) and registers the consumer.
- Exposes REST API on `:8080` with controllers under `/api/*` and enables CORS for `http://localhost:3000`.

3) Scraper starts
- Loads environment, initializes Kafka consumer (topic `scraper-control`) and Kafka producer (topic `fbreaper-topic`).
- Waits for commands from the backend via Kafka.

4) Frontend starts
- Nginx serves the built Vite app, proxies `/api/*` to `backend:8080` in Docker.
- UI provides controls to send scrape commands and to visualize stats/posts.

5) Trigger a scrape
- UI calls `POST /api/scraper/scrapeByKeyword?keyword=osint`.
- Backend publishes JSON command to Kafka topic `scraper-control`.
- Scraper consumes the command, executes Playwright scraping, runs NLP, and publishes enriched posts to `fbreaper-topic`.
- Backend consumes `fbreaper-topic`, parses messages, persists to Neo4j, and exposes them on `/api/data/*`.

### Data Graph Model (Neo4j)

The system stores data primarily as graph entities to enable traversals and link analysis.

- Nodes:
  - `Post` node with properties: `id` (internal), `postId`, `content`, `author`, `timestamp`, `language`, `sentiment`, `hashtags`, `postType`, `createdTime`.
  - `Comment` node with properties: `id` (internal), `commentId`, `content`, `author`, `timestamp`, `language`, `sentiment`, `createdTime`.
  - `GraphNode` (generic representation for link/network analysis; type/name/metadata).

- Relationships:
  - `(:Post)-[:HAS_COMMENT]->(:Comment)` linking comments to posts.
  - `(:GraphNode)-[:LINKS_TO]->(:GraphNode)` for arbitrary link analysis when used.

This schema supports:
- Keyword searches (content-based),
- Temporal filtering (timestamp ranges),
- Network visualizations and link analysis using `GraphNode` and `GraphRelationship`.

### Backend (Java) Source Files

- `FBReaperV1Application.java`
  - Spring Boot entry point.

- `config/KafkaConfig.java`
  - Configures producer/consumer factories, listener container, and declares topics `fbreaper-topic`, `scraper-control`.

- `config/KafkaTopicConfig.java`
  - Additional example topics scaffold (e.g., fb-posts, fb-comments, fb-link-analysis). Can be extended for more granular topics.

- `config/Neo4jConfig.java`
  - Neo4j driver config using Spring Data Neo4j. Reads connection properties/environment.

- `config/SecurityConfig.java`
  - Disables CSRF and permits all requests by default (dev). Replace with real auth in production.

- `controller/HealthController.java`
  - `GET /api/health` simple health check.

- `controller/ScraperController.java`
  - `POST /api/scraper/start`: publishes `{action:"start"}` to `scraper-control`.
  - `POST /api/scraper/scrapeByKeyword?keyword=...`: publishes command with keyword.
  - `GET /api/scraper/status`: returns current status maintained by `ScraperService`.

- `controller/DataController.java`
  - `GET /api/data/posts`: paginated posts list.
  - `GET /api/data/posts/search`: content keyword search.
  - `GET /api/data/posts/{postId}/comments`: comments list (sample implementation).
  - `GET /api/data/stats`: counts/derived stats.
  - `POST /api/data/ingest`: accepts a `KafkaMessage` and relays to Kafka; used for testing ingestion.

- `controller/LinkAnalysisController.java`
  - `GET /api/network/graph`: returns network nodes/links from Neo4j via `LinkAnalysisService`.
  - `GET /api/network/link-analysis`: placeholder for URL-based link analysis.

- `kafka/KafkaProducerService.java`
  - Thin wrapper around `KafkaTemplate` to send messages.

- `kafka/KafkaConsumerService.java`
  - `@KafkaListener` on `fbreaper-topic`: parses messages (posts), updates scraper progress via `ScraperService`, persists via `DataService`.

- `service/ScraperService.java`
  - Tracks scraper lifecycle and progress and sends control messages to `scraper-control`.

- `service/DataService.java`
  - Orchestrates persistence and query operations for posts/comments.
  - `processIncomingData(String message)`: parses JSON posts, maps to `Post`, saves via `PostRepository`.

- `service/PostService.java`, `service/CommentService.java`
  - Example service layers for post/comment operations.

- `service/LinkAnalysisService.java`
  - Fetches/constructs graph data for network endpoints.

- `repository/PostRepository.java`
  - SDN repository with convenience finders: by postId, content, and timestamp range.

- `repository/CommentRepository.java`
  - SDN repository with content and timestamp query methods.

- `repository/GraphNodeRepository.java`, `GraphRelationshipRepository.java`
  - Repositories for generic graph entities.

- `repository/CustomQueryRepository(.impl)`
  - Place for custom Cypher queries when derived methods are insufficient.

- `model/Post.java`, `model/Comment.java`
  - Neo4j `@Node` entities. `Post` contains a `List<Comment>` via `HAS_COMMENT` relationship.

- `model/GraphNode.java`, `model/GraphRelationship.java`
  - Generic nodes/edges for link analysis and visualization.

- `model/KafkaMessage.java`
  - Simple wrapper for `/api/data/ingest` to send type/payload to Kafka.

- `dto/PostDTO.java`, `dto/CommentDTO.java`, `dto/LinkAnalysisResultDTO.java`
  - DTOs returned to the frontend.

- `common/EntityMapper.java`
  - Maps entities to DTOs (author/content/timestamp/hashtags/language/sentiment for posts; analogous for comments).

- `common/GlobalExceptionHandler.java`
  - Centralized exception handling for controllers.

- `exception/PostNotFoundException.java`, `exception/ScraperException.java`
  - Domain-specific exceptions.

- `util/GraphUtils.java`
  - Utility methods to help construct/transform graph responses.

### Scraper (Python) Source Files

- `main.py`
  - Loads env, wires `FacebookScraper`, `NLPPipeline`, `KafkaProducerClient`, `KafkaConsumerClient`.
  - Async loop: listens to `scraper-control`, on `scrapeByKeyword` runs scraping and NLP, formats posts, publishes to `fbreaper-topic`.

- `scraper/facebook_scraper.py`
  - Playwright-based headless Chromium scraper using a persistent `fb_session` for logged-in browsing.
  - `scrape_keyword(keyword)`: searches, scrolls, collects post elements, extracts content/author/timestamp/comments.

- `nlp/nlp_pipeline.py`
  - Language detection (langdetect), sentiment (VADER), hashtag extraction, NER (spaCy), optional translation via LibreTranslate.

- `kafka_client/kafka_producer.py`
  - Synchronous Kafka producer with JSON value serializer.

- `kafka_client/kafka_consumer.py`
  - Async Kafka consumer for control commands with JSON deserializer.

- `requirements.txt`
  - Playwright, langdetect, vaderSentiment, spacy, kafka-python, aiokafka, aiohttp, python-dotenv.

### Message Contracts and Flow

- Scraper publishes each enriched post to `fbreaper-topic` with fields:
  - `postId`, `content`, `author`, `timestamp`, `language`, `sentiment`, `postType`, `createdTime`, `hashtags`.
- Backend consumer parses JSON and persists as `Post` node with optional `Comment` nodes.
- Frontend queries paginated `/api/data/posts` and displays rows (author/content/timestamp/language/sentiment).

### Operational Details and Small Nuances

- CORS and proxying:
  - In Docker, the frontend container proxies `/api/*` to the backend service.
  - For local dev, Vite calls `http://localhost:8080/api` via `VITE_API_BASE_URL`.

- Kafka topics creation:
  - Backend declares topics as beans; brokers auto-create if not present.

- Playwright session management:
  - The scraper uses `launch_persistent_context` with `fb_session` folder. For initial login, run non-headless locally, sign in once, then reuse that session.

- Error handling:
  - Backend `GlobalExceptionHandler` returns structured errors; scraper logs exceptions and continues.

- Scaling:
  - Kafka partitions can be increased and consumer groups adjusted for horizontal scaling of scraper/backend consumers.

- Security and production hardening:
  - Replace permissive `SecurityConfig` with real authN/Z, add rate limiting, request validation, secrets management, proper logging/metrics.