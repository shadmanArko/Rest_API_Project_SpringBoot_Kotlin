package com.arko.analytics.service

import com.arko.analytics.dto.AnalyticsEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class AnalyticsConsumer(
    private val clickHouseService: ClickHouseService
) {
    private val logger = LoggerFactory.getLogger(AnalyticsConsumer::class.java)

    @KafkaListener(topics = ["normalized-events"], groupId = "analytics-group")
    fun consume(event: AnalyticsEvent) {
        logger.info("Consumed event: {}", event)
        try {
            clickHouseService.insertEvent(event)
            logger.info("Saved event to ClickHouse")
        } catch (e: Exception) {
            logger.error("Failed to save event to ClickHouse", e)
        }
    }
}
