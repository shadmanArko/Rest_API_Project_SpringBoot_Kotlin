package com.arko.analytics.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TimePointDto(
    val timestamp: LocalDateTime,
    val impressions: Long,
    val clicks: Long,
    val spend: BigDecimal,
    val orders: Long,
    val revenue: BigDecimal
)
