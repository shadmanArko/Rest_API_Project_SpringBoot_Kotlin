package com.arko.analytics.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun rawEventsTopic(): NewTopic {
        return TopicBuilder.name("raw-events")
            .partitions(1) // Start simple, scale later
            .replicas(1)
            .build()
    }
    
    @Bean
    fun processedEventsTopic(): NewTopic {
        return TopicBuilder.name("normalized-events")
             .partitions(1)
             .replicas(1)
             .build()
    }
}
