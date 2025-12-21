package com.arko.analytics.dto

import java.math.BigDecimal

data class CampaignSummaryDto(
    val campaignId: String,
    val source: String,
    val impressions: Long,
    val clicks: Long,
    val spend: BigDecimal,
    val revenue: BigDecimal,
    val orders: Long,
    val roas: BigDecimal
)
