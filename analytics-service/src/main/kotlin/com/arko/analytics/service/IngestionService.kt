package com.arko.analytics.service

import com.arko.analytics.dto.AnalyticsEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class IngestionService(
    private val kafkaTemplate: KafkaTemplate<String, AnalyticsEvent>
) {
    private val logger = LoggerFactory.getLogger(IngestionService::class.java)
    private val TOPIC = "normalized-events"

    fun ingestEvent(event: AnalyticsEvent) {
        logger.info("Ingesting event: {}", event)
        // In a real scenario, we might first save to S3 (Raw) here or via a separate consumer.
        // For MVP phase 1, we push directly to the normalized topic.
        kafkaTemplate.send(TOPIC, event.eventId.toString(), event)
    }
}
