package com.arko.analytics.dto

import java.math.BigDecimal

data class DashboardOverview(
    val totalImpressions: Long,
    val totalClicks: Long,
    val totalSpend: BigDecimal,
    val totalRevenue: BigDecimal,
    val roas: BigDecimal,
    val cpa: BigDecimal
)

data class CampaignMetric(
    val campaignId: String,
    val source: String,
    val impressions: Long,
    val clicks: Long,
    val spend: BigDecimal,
    val revenue: BigDecimal
)
