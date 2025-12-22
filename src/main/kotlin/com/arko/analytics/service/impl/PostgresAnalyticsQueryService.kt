package com.arko.analytics.service.impl

import com.arko.analytics.dto.*
import com.arko.analytics.repository.RawEventRepository
import com.arko.analytics.service.AnalyticsQueryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service("postgresQueryService")
class PostgresAnalyticsQueryService(
    private val rawRepo: RawEventRepository
) : AnalyticsQueryService {

    override fun getOverview(from: LocalDateTime, to: LocalDateTime, platform: String?): OverviewDto {
        val fromInstant = from.toInstant(ZoneOffset.UTC)
        val toInstant = to.toInstant(ZoneOffset.UTC)
        
        val events = if (platform != null) {
            rawRepo.findAllByPlatformAndEventTimeBetween(platform, fromInstant, toInstant)
        } else {
            rawRepo.findAllByEventTimeBetween(fromInstant, toInstant)
        }

        var spend = BigDecimal.ZERO
        var revenue = BigDecimal.ZERO
        var orders = 0L
        var impressions = 0L

        events.forEach { e ->
            when (e.eventType) {
                "spend" -> spend = spend.add(BigDecimal.valueOf(e.amount ?: 0.0))
                "revenue" -> revenue = revenue.add(BigDecimal.valueOf(e.amount ?: 0.0))
                "order" -> orders++
                "impression" -> impressions++
            }
        }

        val roas = if (spend.signum() == 0) BigDecimal.ZERO else revenue.divide(spend, 4, BigDecimal.ROUND_HALF_UP)
        val avgCpa = if (orders == 0L) BigDecimal.ZERO else spend.divide(BigDecimal.valueOf(orders), 4, BigDecimal.ROUND_HALF_UP)

        return OverviewDto(spend, revenue, roas, orders, impressions, avgCpa)
    }

    override fun getCampaignMetrics(campaignId: String, from: LocalDateTime, to: LocalDateTime, granularity: String): CampaignMetricsDto {
        val fromInstant = from.toInstant(ZoneOffset.UTC)
        val toInstant = to.toInstant(ZoneOffset.UTC)
        val events = rawRepo.findAllByCampaignIdAndEventTimeBetween(campaignId, fromInstant, toInstant)

        val series = events.groupBy { 
            val dt = it.eventTime.atZone(ZoneOffset.UTC).toLocalDateTime()
            if (granularity == "day") dt.toLocalDate().atStartOfDay() else dt.withMinute(0).withSecond(0).withNano(0)
        }.map { (ts, group) ->
            var spend = BigDecimal.ZERO
            var revenue = BigDecimal.ZERO
            var orders = 0L
            var impressions = 0L
            var clicks = 0L

            group.forEach { e ->
                when (e.eventType) {
                    "spend" -> spend = spend.add(BigDecimal.valueOf(e.amount ?: 0.0))
                    "revenue" -> revenue = revenue.add(BigDecimal.valueOf(e.amount ?: 0.0))
                    "order" -> orders++
                    "impression" -> impressions++
                    "click" -> clicks++
                }
            }
            TimePointDto(ts, impressions, clicks, spend, orders, revenue)
        }.sortedBy { it.timestamp }

        return CampaignMetricsDto(campaignId, series)
    }

    override fun getCampaignOverview(from: LocalDateTime, to: LocalDateTime): List<CampaignOverviewDto> {
        val fromInstant = from.toInstant(ZoneOffset.UTC)
        val toInstant = to.toInstant(ZoneOffset.UTC)
        val events = rawRepo.findAllByEventTimeBetween(fromInstant, toInstant)

        return events.filter { it.campaignId != null }.groupBy { it.campaignId }.map { (id, group) ->
            var cost = BigDecimal.ZERO
            var revenue = BigDecimal.ZERO
            group.forEach { e ->
                when (e.eventType) {
                    "spend" -> cost = cost.add(BigDecimal.valueOf(e.amount ?: 0.0))
                    "revenue" -> revenue = revenue.add(BigDecimal.valueOf(e.amount ?: 0.0))
                }
            }
            CampaignOverviewDto(
                campaignId = id!!,
                cost = cost,
                revenue = revenue,
                profit = revenue.subtract(cost),
                roas = if (cost.signum() == 0) BigDecimal.ZERO else revenue.divide(cost, 4, BigDecimal.ROUND_HALF_UP)
            )
        }.sortedByDescending { it.revenue }
    }

    override fun getDailyFinancials(from: LocalDateTime, to: LocalDateTime, companyId: String?): List<DailyFinancialDto> {
        // Fallback strategy: since RawEvent doesn't have financial account types, we might not be able to fully emulate this.
        // However, we can return empty or partial data. For now, empty list as this table specifically comes from accounting mv.
        return emptyList()
    }

    override fun getMonthlyPnL(from: LocalDateTime, to: LocalDateTime, companyId: String?): List<MonthlyPnLDto> {
        return emptyList()
    }
}
