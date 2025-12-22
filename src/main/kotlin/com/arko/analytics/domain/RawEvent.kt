package com.arko.analytics.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(name = "raw_events", indexes = [
    Index(name = "idx_raw_events_campaign", columnList = "campaignId"),
    Index(name = "idx_raw_events_ts", columnList = "eventTime")
])
data class RawEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    val platform: String? = null,
    val accountId: String? = null,
    val campaignId: String? = null,
    val adId: String? = null,
    val eventType: String = "",
    val eventTime: Instant = Instant.now(),
    val amount: Double? = null,
    val currency: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val payload: String? = null,

    val ingestedAt: Instant = Instant.now()
)
