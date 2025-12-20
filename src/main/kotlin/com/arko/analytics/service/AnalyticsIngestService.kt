package com.arko.analytics.service

import com.arko.analytics.dto.EventDto
import com.arko.analytics.domain.RawEvent
import com.arko.analytics.kafka.EventProducer
import com.arko.analytics.repository.RawEventRepository
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsIngestService(
    private val rawRepo: RawEventRepository,
    private val producer: EventProducer
) {

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    /**
     * Ingests raw analytics events.
     * This is WRITE-ONLY, append-only, and idempotent.
     */
    @Transactional
    fun ingest(event: EventDto) {

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
            payload = event.payload?.let { objectMapper.writeValueAsString(it) }
        )

        // 1️⃣ Persist raw (source of truth)
        rawRepo.save(raw)

        // 2️⃣ Publish normalized event for downstream consumers
        producer.publish(
            key = event.eventId.toString(),
            value = objectMapper.writeValueAsString(event)
        )
    }
}
