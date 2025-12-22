package com.arko.analytics.assembler

import com.arko.analytics.dto.CampaignOverviewDto
import com.arko.analytics.dto.DailyFinancialDto
import com.arko.analytics.dto.MonthlyPnLDto
import com.arko.analytics.dto.dashboard.AmountByDateDto
import com.arko.analytics.dto.dashboard.CampaignSummaryDto
import com.arko.analytics.dto.dashboard.DashboardResponseDto
import com.arko.analytics.dto.dashboard.FinancialSummaryDto
import com.arko.analytics.dto.dashboard.FinancialTrendDto
import java.math.BigDecimal
import kotlin.collections.filter

object DashboardAssembler {

    fun assemble(
        daily: List<DailyFinancialDto>,
        pnl: List<MonthlyPnLDto>,
        campaigns: List<CampaignOverviewDto>
    ): DashboardResponseDto {

        // Daily totals
        val totalRevenue = daily
            .filter { it.accountType == "REVENUE" }
            .sumOf { it.amount }  // <- fixed

        val totalExpense = daily
            .filter { it.accountType == "EXPENSE" }
            .sumOf { it.amount }  // <- fixed

        val netProfit = totalRevenue.subtract(totalExpense)

        val profitMargin = if (totalRevenue.compareTo(BigDecimal.ZERO) == 0)
            BigDecimal.ZERO
        else netProfit
            .multiply(BigDecimal.valueOf(100))
            .divide(totalRevenue, 2, BigDecimal.ROUND_HALF_UP)

        return DashboardResponseDto(
            summary = FinancialSummaryDto(
                totalRevenue,
                totalExpense,
                netProfit,
                profitMargin
            ),
            trends = FinancialTrendDto(
                dailyRevenue = daily
                    .filter { it.accountType == "REVENUE" }
                    .map { AmountByDateDto(it.date.toString(), it.amount) },  // <- fixed
                dailyExpense = daily
                    .filter { it.accountType == "EXPENSE" }
                    .map { AmountByDateDto(it.date.toString(), it.amount) }   // <- fixed
            ),
            campaigns = campaigns.map {
                val roi = if (it.cost.compareTo(BigDecimal.ZERO) == 0)
                    BigDecimal.ZERO
                else
                    it.revenue
                        .divide(it.cost, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100))  // ROI in %
                CampaignSummaryDto(
                    campaignId = it.campaignId.toString(),
                    cost = it.cost,
                    revenue = it.revenue,
                    roi = roi
                )
            }
        )
    }
}
