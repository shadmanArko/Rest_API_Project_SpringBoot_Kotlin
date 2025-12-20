package com.arko.analytics.dto

import java.math.BigDecimal
import java.util.UUID

data class CampaignKpiResponse(
    val campaignId: UUID,
    val totalCost: BigDecimal,
    val totalRevenue: BigDecimal,
    val roi: BigDecimal
)