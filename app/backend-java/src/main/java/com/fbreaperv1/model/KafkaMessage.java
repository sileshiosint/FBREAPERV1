package com.fbreaperv1.model;

import java.util.Map;

public class KafkaMessage {
    private String type;
    private Map<String, Object> payload;

    public KafkaMessage() {}

    public KafkaMessage(String type, Map<String, Object> payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
