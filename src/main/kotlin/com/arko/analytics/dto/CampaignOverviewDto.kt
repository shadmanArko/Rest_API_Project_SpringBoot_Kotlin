package com.arko.analytics.dto

import java.math.BigDecimal

data class CampaignOverviewDto(
    val campaignId: String,
    val cost: BigDecimal,
    val revenue: BigDecimal,
    val profit: BigDecimal,
    val roas: BigDecimal
)
