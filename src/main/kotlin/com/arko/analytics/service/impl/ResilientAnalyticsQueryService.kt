package com.arko.analytics.service.impl

import com.arko.analytics.dto.*
import com.arko.analytics.service.AnalyticsQueryService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Primary
class ResilientAnalyticsQueryService(
    @Qualifier("postgresQueryService") private val postgresService: AnalyticsQueryService,
    @Qualifier("clickHouseQueryService") private val clickHouseService: AnalyticsQueryService? = null
) : AnalyticsQueryService {

    private val log = LoggerFactory.getLogger(ResilientAnalyticsQueryService::class.java)

    override fun getOverview(from: LocalDateTime, to: LocalDateTime, platform: String?): OverviewDto {
        if (clickHouseService != null) {
            try {
                val result = clickHouseService.getOverview(from, to, platform)
                if (result.totalSpend.signum() > 0 || result.totalRevenue.signum() > 0 || result.orders > 0) {
                    return result
                }
                log.info("ClickHouse overview empty, falling back to Postgres")
            } catch (e: Exception) {
                log.error("ClickHouse overview failed, falling back to Postgres: {}", e.message)
            }
        }
        return postgresService.getOverview(from, to, platform)
    }

    override fun getCampaignMetrics(campaignId: String, from: LocalDateTime, to: LocalDateTime, granularity: String): CampaignMetricsDto {
        if (clickHouseService != null) {
            try {
                val result = clickHouseService.getCampaignMetrics(campaignId, from, to, granularity)
                if (result.series.isNotEmpty()) return result
            } catch (e: Exception) {
                log.error("ClickHouse metrics failed: {}", e.message)
            }
        }
        return postgresService.getCampaignMetrics(campaignId, from, to, granularity)
    }

    override fun getCampaignOverview(from: LocalDateTime, to: LocalDateTime): List<CampaignOverviewDto> {
        if (clickHouseService != null) {
            try {
                val result = clickHouseService.getCampaignOverview(from, to)
                if (result.isNotEmpty()) return result
            } catch (e: Exception) {
                log.error("ClickHouse campaign overview failed: {}", e.message)
            }
        }
        return postgresService.getCampaignOverview(from, to)
    }

    override fun getDailyFinancials(from: LocalDateTime, to: LocalDateTime, companyId: String?): List<DailyFinancialDto> {
        if (clickHouseService != null) {
            try {
                val result = clickHouseService.getDailyFinancials(from, to, companyId)
                if (result.isNotEmpty()) return result
            } catch (e: Exception) {
                log.error("ClickHouse daily financials failed: {}", e.message)
            }
        }
        return postgresService.getDailyFinancials(from, to, companyId)
    }

    override fun getMonthlyPnL(from: LocalDateTime, to: LocalDateTime, companyId: String?): List<MonthlyPnLDto> {
        if (clickHouseService != null) {
            try {
                val result = clickHouseService.getMonthlyPnL(from, to, companyId)
                if (result.isNotEmpty()) return result
            } catch (e: Exception) {
                log.error("ClickHouse monthly pnl failed: {}", e.message)
            }
        }
        return postgresService.getMonthlyPnL(from, to, companyId)
    }
}
