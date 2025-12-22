package com.arko.analytics.dto.dashboard

import java.math.BigDecimal

data class CampaignSummaryDto(
    val campaignId: String,
    val cost: BigDecimal,
    val revenue: BigDecimal,
    val roi: BigDecimal
)
