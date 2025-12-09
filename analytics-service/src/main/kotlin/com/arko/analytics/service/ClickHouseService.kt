package com.arko.analytics.service

import com.arko.analytics.dto.AnalyticsEvent
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class ClickHouseService(
    private val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(ClickHouseService::class.java)

    fun insertEvent(event: AnalyticsEvent) {
        val sql = """
            INSERT INTO normalized_events (
                event_id, source, account_id, campaign_id, ad_id, creative_id,
                event_type, amount, currency, timestamp
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.update(
            sql,
            event.eventId.toString(),
            event.source,
            event.accountId,
            event.campaignId,
            event.adId,
            event.creativeId,
            event.eventType,
            event.amount,
            event.currency,
            event.timestamp
        )
        
        logger.info("Inserted event {} into ClickHouse", event.eventId)
    }
}
