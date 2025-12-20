package com.arko.analytics.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class EventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${app.kafka.topic}") private val topic: String,
    @Value("\${app.kafka.enabled:true}") private val enabled: Boolean
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(EventProducer::class.java)

    fun publish(key: String, value: String) {
        if (!enabled) return

        try {
            kafkaTemplate.send(topic, key, value)
        } catch (e: Exception) {
            logger.error("Failed to publish event to Kafka (key: $key): ${e.message}")
        }
    }
}
