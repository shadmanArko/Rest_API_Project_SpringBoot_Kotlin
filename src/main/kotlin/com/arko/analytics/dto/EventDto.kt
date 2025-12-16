package com.arko.analytics.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

data class EventDto(
    val eventId: UUID = UUID.randomUUID(),
    @field:NotBlank val platform: String,       // "meta", "google", "stripe", "web"
    val accountId: String?,
    val campaignId: String?,
    val adId: String?,
    @field:NotBlank val eventType: String,     // "impression","click","spend","order","refund"
    @field:NotNull val timestamp: Instant,
    val amount: Double? = null,
    val currency: String? = null,
    val payload: Map<String, Any>? = null
)