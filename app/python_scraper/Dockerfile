FROM mcr.microsoft.com/playwright/python:v1.45.0-jammy
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt \
    && python -m playwright install --with-deps chromium \
    && python -m spacy download en_core_web_sm
COPY . .
ENV KAFKA_BROKER=localhost:9092 \
    KAFKA_SCRAPE_TOPIC=fbreaper-topic \
    KAFKA_COMMANDS_TOPIC=scraper-control \
    LIBRETRANSLATE_URL=http://libretranslate:5000/translate
CMD ["python", "main.py"]