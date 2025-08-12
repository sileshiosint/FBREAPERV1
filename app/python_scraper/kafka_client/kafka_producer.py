from kafka import KafkaProducer
import json
import logging

class KafkaProducerClient:
    def __init__(self, broker):
        self.producer = KafkaProducer(
            bootstrap_servers=broker,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda k: k.encode("utf-8") if isinstance(k, str) else k
        )

    def send_message(self, topic, message, key=None):
        try:
            self.producer.send(topic, value=message, key=key)
            self.producer.flush()
            logging.info(f"Sent message to topic {topic} with key {key}")
        except Exception as ex:
            logging.error(f"Error sending message to Kafka: {ex}")