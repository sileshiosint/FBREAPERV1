import asyncio
import logging
import os
from dotenv import load_dotenv

from scraper.facebook_scraper import FacebookScraper
from nlp.nlp_pipeline import NLPPipeline
from kafka_client.kafka_producer import KafkaProducerClient
from kafka_client.kafka_consumer import KafkaConsumerClient

load_dotenv()

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s"
)

KAFKA_BROKER = os.getenv("KAFKA_BROKER", "localhost:9092")
KAFKA_SCRAPE_TOPIC = os.getenv("KAFKA_SCRAPE_TOPIC", "fbreaper-topic")
KAFKA_COMMANDS_TOPIC = os.getenv("KAFKA_COMMANDS_TOPIC", "scraper-control")
LIBRETRANSLATE_URL = os.getenv("LIBRETRANSLATE_URL", "http://localhost:5001/translate")

stop_requested = False

async def emit_posts(posts, nlp, producer):
    enriched_posts = []
    for post in posts:
        nlp_data = await nlp.process_post(post)
        enriched_post = {**post, **nlp_data}
        formatted_post = {
            "postId": f"post_{len(enriched_posts)}",
            "content": enriched_post.get("content", ""),
            "author": enriched_post.get("author", ""),
            "timestamp": enriched_post.get("timestamp", ""),
            "language": enriched_post.get("language", "en"),
            "sentiment": enriched_post.get("sentiment", "neutral"),
            "postType": "post",
            "createdTime": enriched_post.get("timestamp", ""),
            "hashtags": enriched_post.get("hashtags", [])
        }
        enriched_posts.append(formatted_post)
    for enriched_post in enriched_posts:
        producer.send_message(KAFKA_SCRAPE_TOPIC, enriched_post)

async def handle_scrape_command(command, scraper, nlp, producer):
    """
    Handles incoming scrape commands.
    """
    global stop_requested
    logging.info(f"Received command: {command}")
    action = command.get('action')
    if action == 'scrapeByKeyword':
        keyword = command.get('keyword')
        posts = await scraper.scrape_keyword(keyword)
        await emit_posts(posts, nlp, producer)
        logging.info(f"Scraped and sent {len(posts)} posts for keyword '{keyword}'")
    elif action == 'start':
        # Default crawl: scrape a few common OSINT-related keywords sequentially
        stop_requested = False
        for kw in ["osint", "security", "threat intel"]:
            if stop_requested:
                break
            posts = await scraper.scrape_keyword(kw)
            await emit_posts(posts, nlp, producer)
            logging.info(f"Scraped and sent {len(posts)} posts for keyword '{kw}'")
            await asyncio.sleep(1)
    elif action == 'stop':
        stop_requested = True
        logging.info("Stop requested; current loop will finish and then halt new work.")
    else:
        logging.warning(f"Unknown command: {command}")

async def main():
    # Initialize components
    scraper = FacebookScraper()
    nlp = NLPPipeline(LIBRETRANSLATE_URL)
    producer = KafkaProducerClient(KAFKA_BROKER)
    consumer = KafkaConsumerClient(KAFKA_BROKER, KAFKA_COMMANDS_TOPIC)

    async def command_loop():
        async for command in consumer.listen():
            await handle_scrape_command(command, scraper, nlp, producer)

    await command_loop()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        logging.info("FBREAPER Scraper microservice stopped.")