# Facebook OSINT Dashboard (FBReaperV1)

A production-ready, containerized OSINT system for Facebook data collection, enrichment, storage, and analysis. The monorepo contains:
- Frontend: Vite + React served by Nginx
- Backend: Java Spring Boot (Web, Security, Kafka, Neo4j)
- Scraper: Python Playwright + NLP (langdetect, spaCy, VADER) + Kafka
- Infra: Kafka + Zookeeper, Neo4j, LibreTranslate (optional but included)

All services are orchestrated with Docker Compose. The backend exposes REST APIs consumed by the frontend. The scraper streams enriched data to Kafka; the backend consumes and persists to Neo4j.

## Architecture

```text
                        ┌───────────────────────────────────────────────────────┐
                        │                         User                          │
                        └───────────────▲───────────────────────────────────────┘
                                        │
                                        │ 1. HTTP(S)
                                        │
                 ┌──────────────────────┴──────────────────────┐
                 │               Frontend (Nginx)              │
                 │         Vite + React, Port :3000            │
                 └───────────────▲───────────────┬─────────────┘
                                 │               │
                 2. /api proxy   │               │ 3. Static assets
                                 │               │
                     ┌───────────┴───────────┐   │
                     │   Backend (Spring)    │◄──┘
                     │ REST, Kafka, Neo4j    │   4. Reads/Writes
                     │ Port :8080            │───────────┐
                     └───────▲───────┬───────┘           │
                             │       │                   │
        5a. Consume posts    │       │ 5b. Produce cmds  │
                             │       │                   │
                  ┌──────────┴───┐   │     ┌─────────────▼────────────┐
                  │    Kafka      │◄──┼────►     Scraper (Python)     │
                  │ :9092         │         │ Playwright + NLP         │
                  └───────────────┘         └───────────▲──────────────┘
                                                        │ 6. HTTP (optional)
                                                        │ LibreTranslate
                                                        │
                                  ┌──────────────────────┴─────────────────────┐
                                  │                Neo4j :7687                │
                                  │ Graph DB for posts, comments, relations   │
                                  └────────────────────────────────────────────┘
```

- Kafka topics:
  - `fbreaper-topic`: scraper → backend (enriched posts/comments)
  - `scraper-control`: backend → scraper (commands)

## End-to-End Flow (Step-by-step)

- Scrape by keyword (user-initiated):
  1. User clicks “Scrape by Keyword” in the frontend and enters a keyword.
  2. Frontend calls `POST /api/scraper/scrapeByKeyword?keyword={k}`.
  3. Backend validates, builds a command message, and produces to `scraper-control`.
  4. Scraper consumes the command, launches Playwright with a persistent Facebook session.
  5. Scraper navigates to relevant pages/results and simulates human-like actions (scrolling, expanding, delays).
  6. Scraper extracts posts, comments, authors, timestamps, URLs, reactions.
  7. NLP pipeline runs: language detection, sentiment, hashtag extraction, basic NER; optional translation via LibreTranslate.
  8. Scraper produces enriched JSON records to `fbreaper-topic` (keyed by `postId`).
  9. Backend Kafka consumer ingests messages, validates/normalizes payloads.
  10. Backend upserts nodes/relationships in Neo4j (posts, comments, hashtags, links).
  11. Frontend polls or requests data from REST endpoints to render tables, stats, and graphs.

- Read data:
  1. Frontend requests posts via `GET /api/data/posts?page=&size=`.
  2. Backend queries Neo4j repositories and returns DTOs.
  3. Frontend renders lists, details, and graph visualizations (D3-ready shapes provided by backend endpoints).

## Data Contracts

- Kafka messages from scraper (actual format):
```json
{
  "postId": "a7a2f...", 
  "author": "John Doe",
  "content": "Post content ...",
  "timestamp": "2024-05-01T12:34:56Z",
  "language": "en",
  "sentiment": 0.42,
  "hashtags": ["#osint", "#analysis"],
  "entities": [{"text": "John", "label": "PERSON"}],
  "translation": null,
  "comments": [
    {"text": "Great!", "author": "Jane", "timestamp": "2024-05-01T12:40:00Z"}
  ],
  "postType": "post",
  "createdTime": "2024-05-01T12:34:56Z"
}
```

- Kafka commands to scraper (example):
```json
{
  "action": "scrapeByKeyword",
  "keyword": "osint",
  "options": { "maxPosts": 25 }
}
```

- REST API (selected):
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

## Persistence Model (Neo4j)

- Nodes:
  - `Post {postId, author, content, timestamp, language, sentiment, hashtags}`
  - `Comment {commentId, postId, author, text, timestamp, sentiment}`
  - `Tag {name}`
  - Optional analytical types: `GraphNode`, `GraphRelationship`
- Relationships:
  - `(Post)-[:HAS_COMMENT]->(Comment)`
  - `(Post)-[:MENTIONS_HASHTAG]->(Tag)`
  - `(Comment)-[:REPLIES_TO]->(Comment)` (if nested)

Sample Cypher:
```cypher
MERGE (p:Post {postId: $postId})
SET p.content=$content, p.author=$author, p.timestamp=$ts,
    p.language=$lang, p.sentiment=$sent
WITH p
UNWIND $hashtags AS tag
MERGE (t:Tag {name: tag})
MERGE (p)-[:MENTIONS_HASHTAG]->(t)
```

## Configuration & Environments

- Docker Compose services (`app/docker-compose.yml`):
  - `neo4j:5.15` (auth `neo4j/neo4jpassword`), ports `7687`, `7474`
  - `zookeeper`, `kafka` (Confluent 7.4)
  - `backend` (Spring Boot), env:
    - `SPRING_NEO4J_URI=bolt://neo4j:7687`
    - `SPRING_NEO4J_AUTHENTICATION_USERNAME=neo4j`
    - `SPRING_NEO4J_AUTHENTICATION_PASSWORD=neo4jpassword`
    - `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`
    - `SPRING_WEB_CORS_ALLOWED_ORIGINS=http://localhost:3000`
  - `libretranslate` (optional translation), ports `5001:5000`
  - `scraper` (Python), env:
    - `KAFKA_BROKER=kafka:9092`
    - `KAFKA_SCRAPE_TOPIC=fbreaper-topic`
    - `KAFKA_COMMANDS_TOPIC=scraper-control`
    - `LIBRETRANSLATE_URL=http://libretranslate:5000/translate`
    - `HEADLESS=true`
    - `DEFAULT_MAX_POSTS=10`
    - persistent session mounted at `python_scraper/fb_session`
  - `frontend` (Nginx serving Vite build)

- Frontend `.env`:
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

- Scraper `.env` (for local runs outside Docker):
```env
KAFKA_BROKER=localhost:9092
KAFKA_SCRAPE_TOPIC=fbreaper-topic
KAFKA_COMMANDS_TOPIC=scraper-control
LIBRETRANSLATE_URL=http://localhost:5001/translate
HEADLESS=true
DEFAULT_MAX_POSTS=10
FB_USER_DATA_DIR=fb_session
```

- Nginx reverse proxy (in container):
```nginx
location /api/ {
  proxy_pass http://backend:8080/api/;
}
```

## First-time Facebook Login (Playwright)

The scraper uses a persistent Chromium context stored in `python_scraper/fb_session`. To create a logged-in session:
- Run the scraper non-headless once and log into Facebook.
- The session will persist in `fb_session` volume and be reused headless thereafter.

Docker example:
```bash
cd app
# Start infra so Kafka is available
docker compose up -d neo4j kafka libretranslate
# Run scraper once interactively to login
docker compose run --rm -e HEADLESS=false scraper python main.py
```

Local example:
```bash
cd app/python_scraper
python3 -m venv venv && source venv/bin/activate
pip install -r requirements.txt
python -m playwright install chromium
python -m spacy download en_core_web_sm
cp .env.example .env
# set HEADLESS=false in .env for first login
python main.py
```

## Local Development

- Using Docker (recommended):
```bash
cd app
docker compose up -d --build
# Frontend: http://localhost:3000
# Backend:  http://localhost:8080/api/health
# Neo4j:    http://localhost:7474 (neo4j/neo4jpassword)
# LibreTranslate: http://localhost:5001
```

- Partial (infra only) + local services:
```bash
cd app
docker compose up -d neo4j kafka libretranslate
```

- Backend (local):
```bash
cd app/backend-java
mvn spring-boot:run
```

- Scraper (local):
```bash
cd app/python_scraper
python3 -m venv venv && source venv/bin/activate
pip install -r requirements.txt
python -m playwright install chromium
python -m spacy download en_core_web_sm
cp .env.example .env  # adjust if needed
python main.py
```

- Frontend (local):
```bash
cd app/frontend
npm install
npm run dev
```

## Operations & Observability

- Start/Stop (helpers):
  - `app/start-system.sh` and `app/stop-system.sh`
- Logs:
  - Docker: `docker compose logs -f <service>`
  - Backend: app logs in container; Spring Actuator can be added
  - Scraper: stdout/stderr in container
- Data backup:
  - Neo4j volumes: `neo4j_data`, `neo4j_logs`, `neo4j_import`, `neo4j_plugins`
- Kafka tips:
  - Prefer keys by `postId` for partitioning
  - Use consumer groups in backend for scalability

## Security Notes

- Respect Facebook TOS and local laws. Use only where legal and authorized.
- Add authentication/authorization (e.g., JWT) before production.
- Store secrets in env vars or secret managers; never in VCS.
- Enable CORS restrictions and tighten Nginx.

## Repository Structure

```text
app/
├─ backend-java/
│  ├─ src/main/java/com/fbreaperv1/...
│  ├─ src/main/resources/application.properties
│  ├─ Dockerfile
│  └─ pom.xml
├─ python_scraper/
│  ├─ scraper/  nlp/  kafka_client/
│  ├─ .env.example  requirements.txt  Dockerfile  main.py
│  └─ fb_session/ (persistent login, ignored)
├─ frontend/
│  ├─ src/  index.html  package.json  Dockerfile  vite.config.ts
├─ docker-compose.yml
├─ start-system.sh
└─ stop-system.sh
```

## Additional Diagrams

Deployment (containers + network):
```text
┌──────────┐   ┌────────────┐   ┌─────────────┐   ┌─────────┐
│ Frontend │   │  Backend   │   │   Kafka     │   │  Neo4j  │
│  :3000   │──►│  :8080     │──►│  :9092      │   │  :7687  │
└──────────┘   └────────────┘   └─────────────┘   └─────────┘
         ▲            │                 ▲                ▲
         └────────────┴─────────────────┴────────────────┘
                    Docker bridge network: fbreaper-network
```

Data pipeline (high level):
```text
Facebook → Scraper (Playwright) → NLP → Kafka(fbreaper-topic) → Backend Consumer → Neo4j → REST → Frontend UI
```

## License

Educational and research use only.