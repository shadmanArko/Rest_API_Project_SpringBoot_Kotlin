package com.arko.analytics.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.UUID

data class EventDto(
    val eventId: UUID = UUID.randomUUID(),
    @JsonAlias("source", "platform_name")
    val platform: String? = null,       // "meta", "google", "stripe", "web"
    val accountId: String? = null,
    val campaignId: String? = null,
    val adId: String? = null,
    @field:NotBlank val eventType: String,     // "impression","click","spend","order","refund"
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    val timestamp: Instant = Instant.now(),
    val amount: Double? = null,
    val currency: String? = null,
    val payload: Map<String, Any>? = null
)