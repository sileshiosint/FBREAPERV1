package com.fbreaperv1.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name("fb-posts")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name("fb-comments")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic linkAnalysisTopic() {
        return TopicBuilder.name("fb-link-analysis")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
