import aiohttp
import re
import logging
from langdetect import detect
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
import spacy

class NLPPipeline:
    def __init__(self, libretranslate_url):
        self.libretranslate_url = libretranslate_url
        self.sentiment_analyzer = SentimentIntensityAnalyzer()
        self.spacy_nlp = spacy.load("en_core_web_sm")

    async def process_post(self, post):
        """
        Enriches post dict with language, sentiment, hashtags, translation, NER.
        """
        content = post.get("content", "")
        language = self.detect_language(content)
        sentiment = self.analyze_sentiment(content)
        hashtags = self.extract_hashtags(content)
        translation = None
        if language != "en":
            translation = await self.translate_to_english(content, language)
            content_en = translation
        else:
            content_en = content
        entities = self.extract_entities(content_en)
        return {
            "language": language,
            "sentiment": sentiment,
            "hashtags": hashtags,
            "translation": translation,
            "entities": entities
        }

    def detect_language(self, text):
        try:
            return detect(text)
        except Exception as ex:
            logging.warning(f"Language detection failed: {ex}")
            return "unknown"

    def analyze_sentiment(self, text):
        try:
            scores = self.sentiment_analyzer.polarity_scores(text)
            return scores['compound']
        except Exception as ex:
            logging.warning(f"Sentiment analysis failed: {ex}")
            return 0.0

    def extract_hashtags(self, text):
        return re.findall(r"#(\w+)", text)

    def extract_entities(self, text):
        doc = self.spacy_nlp(text)
        return [{
            "text": ent.text,
            "label": ent.label_
        } for ent in doc.ents]

    async def translate_to_english(self, text, source_lang):
        async with aiohttp.ClientSession() as session:
            try:
                async with session.post(
                    self.libretranslate_url,
                    json={
                        "q": text,
                        "source": source_lang,
                        "target": "en"
                    }
                ) as resp:
                    data = await resp.json()
                    return data.get("translatedText", "")
            except Exception as ex:
                logging.warning(f"Translation failed: {ex}")
                return text