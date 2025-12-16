package com.arko.analytics.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.arko.analytics.dto.EventDto
import com.arko.analytics.domain.RawEvent
import com.arko.analytics.kafka.EventProducer
import com.arko.analytics.repository.RawEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val rawRepo: RawEventRepository,
    private val producer: EventProducer
) {
    private val om = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Transactional
    fun ingest(event: EventDto) {
        // Save raw
        val raw = RawEvent(
            id = event.eventId,
            platform = event.platform,
            accountId = event.accountId,
            campaignId = event.campaignId,
            adId = event.adId,
            eventType = event.eventType,
            eventTime = event.timestamp,
            amount = event.amount,
            currency = event.currency,
            payload = event.payload?.let { om.writeValueAsString(it) }
        )
        rawRepo.save(raw)

        // Publish normalized JSON to Kafka for downstream processing
        val message = om.writeValueAsString(event)
        producer.publish(event.eventId.toString(), message)
    }
}
