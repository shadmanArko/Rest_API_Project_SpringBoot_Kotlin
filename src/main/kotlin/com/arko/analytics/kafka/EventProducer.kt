package com.arko.analytics.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class EventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${app.kafka.topic}") private val topic: String
) {
    fun publish(key: String, value: String) {
        kafkaTemplate.send(topic, key, value)
    }
}
