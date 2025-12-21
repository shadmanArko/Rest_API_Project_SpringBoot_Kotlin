package com.arko.analytics.dto

import java.math.BigDecimal

data class PlatformMetricDto(
    val platform: String,
    val impressions: Long,
    val clicks: Long,
    val spend: BigDecimal,
    val revenue: BigDecimal,
    val share: Double
)

data class PlatformTrendDto(
    val platform: String,
    val series: List<TimePointDto>
)
