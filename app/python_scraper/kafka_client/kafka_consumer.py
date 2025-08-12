import json
from aiokafka import AIOKafkaConsumer
import asyncio
import logging

class KafkaConsumerClient:
    def __init__(self, broker, topic):
        self.broker = broker
        self.topic = topic

    async def listen(self):
        consumer = AIOKafkaConsumer(
            self.topic,
            bootstrap_servers=self.broker,
            group_id="fbreaper-scraper",
            value_deserializer=lambda v: json.loads(v.decode("utf-8")),
            auto_offset_reset="earliest"
        )
        await consumer.start()
        try:
            async for msg in consumer:
                logging.info(f"Received command: {msg.value}")
                    # Java backend sends commands as JSON strings
                yield msg.value
        finally:
            await consumer.stop()