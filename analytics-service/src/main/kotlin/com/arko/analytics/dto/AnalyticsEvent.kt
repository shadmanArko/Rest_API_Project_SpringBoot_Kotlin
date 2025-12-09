package com.arko.analytics.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class AnalyticsEvent(
    val eventId: UUID = UUID.randomUUID(),
    val source: String,             // e.g., "meta_ads", "google_ads", "web"
    val accountId: String? = null,  // Platform account ID
    val campaignId: String? = null,
    val adId: String? = null,
    val creativeId: String? = null,
    val eventType: String,          // e.g., "impression", "click", "order"
    val amount: BigDecimal? = null, // e.g., spend amount or revenue
    val currency: String? = null,
    val timestamp: Instant = Instant.now(),
    val payload: Map<String, Any>? = null // Raw payload for auditing
)
