package com.arko.analytics.dto.dashboard

import java.math.BigDecimal

data class DashboardResponseDto(
    val summary: FinancialSummaryDto,
    val trends: FinancialTrendDto,
    val campaigns: List<CampaignSummaryDto>
)
