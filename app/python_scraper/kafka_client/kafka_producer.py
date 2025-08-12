from kafka import KafkaProducer
import json
import logging

class KafkaProducerClient:
    def __init__(self, broker):
        self.producer = KafkaProducer(
            bootstrap_servers=broker,
            value_serializer=lambda v: json.dumps(v).encode("utf-8")
        )

    def send_message(self, topic, message):
        try:
            # Pass dicts or strings; serializer handles JSON encoding
            self.producer.send(topic, message)
            self.producer.flush()
            logging.info(f"Sent message to topic {topic}")
        except Exception as ex:
            logging.error(f"Error sending message to Kafka: {ex}")