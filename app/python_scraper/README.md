# FBREAPER Facebook Scraper Microservice

A modular Python microservice for the FBREAPER OSINT project. It scrapes Facebook using Playwright, enriches with NLP, and integrates with the Java backend via Kafka.

---

## Features

- **Facebook Scraper:** Uses Playwright with a persistent logged-in session.
- **Human-like Browsing:** Simulates typing, scrolling, and navigation.
- **Deep Extraction:** Posts, nested comments, reactions, user/timestamp metadata.
- **Dynamic Content Handling:** Infinite scroll, "See more" expansion.
- **Multilingual NLP:** Detects language, sentiment, hashtags, NER, and translation (LibreTranslate).
- **Kafka Integration:** Sends enriched data to backend topics, receives scrape commands.
- **Error Handling & Logging:** Robust logging and error reporting.

---

## Quick Start

### 1. Install dependencies

```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
playwright install
python -m spacy download en_core_web_sm
```

### 2. Configure environment variables

Copy `.env.example` to `.env` and edit as needed.

- Ensure `KAFKA_BROKER`, topic names, and `LIBRETRANSLATE_URL` are correct.
- Place your Facebook login session (cookies) in `fb_session` directory for Playwright.

### 3. Run the microservice

```bash
python main.py
```

### 4. Sending Commands

Send JSON messages to the `scraper-commands` Kafka topic, e.g.:
```json
{"action": "scrapeByKeyword", "keyword": "osint"}
```

---

## Project Structure

- `main.py` — entrypoint, wiring all components together.
- `scraper/` — Facebook scraping logic (Playwright).
- `nlp/` — NLP pipeline (langdetect, sentiment, hashtags, NER, translation).
- `kafka_client/` — Kafka producer/consumer wrappers.
- `.env.example` — configuration template.
- `README.md` — this file.

---

## Integration

- **Kafka message format:** Aligns with Java DTOs (`PostDTO`, `CommentDTO`, etc).
- **Topics:** Configurable via `.env`.
- **Extensible:** Add more NLP/graph features as needed.

---

## Troubleshooting

- Make sure Playwright has a logged-in Facebook session in `fb_session`.
- Kafka and LibreTranslate must be running and reachable.
- For headless scraping, set `headless=True` in `facebook_scraper.py`.

---

## License

MIT