package com.arko.analytics.service

import com.arko.analytics.dto.*
import java.time.LocalDateTime

interface AnalyticsQueryService {

    // -------- MARKETING --------

    fun getOverview(
        from: LocalDateTime,
        to: LocalDateTime,
        platform: String? = null
    ): OverviewDto

    fun getCampaignMetrics(
        campaignId: String,
        from: LocalDateTime,
        to: LocalDateTime,
        granularity: String = "hour"
    ): CampaignMetricsDto

    fun getCampaignOverview(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<CampaignOverviewDto>

    // -------- FINANCIAL --------

    fun getDailyFinancials(
        from: LocalDateTime,
        to: LocalDateTime,
        companyId: String? = null
    ): List<DailyFinancialDto>

    fun getMonthlyPnL(
        from: LocalDateTime,
        to: LocalDateTime,
        companyId: String? = null
    ): List<MonthlyPnLDto>
}
