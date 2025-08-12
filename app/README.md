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