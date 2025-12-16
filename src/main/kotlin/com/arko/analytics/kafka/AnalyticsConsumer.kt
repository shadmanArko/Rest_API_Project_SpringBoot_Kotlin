package com.arko.analytics.kafka

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.arko.analytics.aggregation.AggregatedMetrics
import com.arko.analytics.aggregation.CampaignHourKey
import com.arko.analytics.clickhouse.ClickHouseWriter
import com.arko.analytics.dto.EventDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
@ConditionalOnProperty(name = ["app.clickhouse.enabled"], havingValue = "true", matchIfMissing = false)
class AnalyticsConsumer(
    private val clickHouseWriter: ClickHouseWriter
) {
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @KafkaListener(topics = ["normalized-events"], groupId = "analytics-aggregator")
    fun consume(message: String) {
        val event = objectMapper.readValue(message, EventDto::class.java)

        val hour = event.timestamp.atZone(ZoneOffset.UTC)
            .toLocalDateTime()
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val key = CampaignHourKey(
            dateHour = hour,
            platform = event.platform,
            accountId = event.accountId,
            campaignId = event.campaignId
        )

        val metrics = AggregatedMetrics().apply {
            when (event.eventType) {
                "impression" -> impressions++
                "click" -> clicks++
                "spend" -> spend += event.amount ?: 0.0
                "order" -> orders++
                "revenue" -> revenue += event.amount ?: 0.0
            }
        }

        clickHouseWriter.write(key, metrics)
    }
}
