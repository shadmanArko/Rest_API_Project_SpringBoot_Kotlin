package com.arko.analytics.dto

import java.math.BigDecimal

data class GrowthMetricDto(
    val label: String,
    val current: Double,
    val previous: Double,
    val changePercent: Double
)

data class KpiDashboardDto(
    val metrics: List<GrowthMetricDto>,
    val conversionRate: Double,
    val ltv: BigDecimal,
    val cac: BigDecimal
)
