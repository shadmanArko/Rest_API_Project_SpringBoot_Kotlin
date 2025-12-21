package com.arko.analytics.controller

import com.arko.analytics.dto.*
import com.arko.analytics.exception.AnalyticsDisabledException
import com.arko.analytics.service.AnalyticsIngestService
import com.arko.analytics.service.AnalyticsQueryService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.math.BigDecimal

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val ingestService: AnalyticsIngestService,
    private val queryService: AnalyticsQueryService?
) {

    // ---------------- INGEST ----------------

    @PostMapping("/events")
    fun postEvent(@RequestBody @Valid dto: EventDto): ResponseEntity<Map<String, Any>> {
        ingestService.ingest(dto)
        return ResponseEntity.accepted().body(
            mapOf(
                "status" to "accepted",
                "eventId" to dto.eventId
            )
        )
    }

    // ---------------- QUERIES ----------------

    @GetMapping("/campaigns")
    fun allCampaigns(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): List<CampaignSummaryDto> =
        queryService?.getAllCampaigns(from, to) ?: throw AnalyticsDisabledException()

    @GetMapping("/financials/daily")
    fun financialsDaily(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): List<FinancialsDto> =
        queryService?.getFinancials(from, to, "day") ?: throw AnalyticsDisabledException()

    @GetMapping("/financials/monthly")
    fun financialsMonthly(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): List<FinancialsDto> =
        queryService?.getFinancials(from, to, "month") ?: throw AnalyticsDisabledException()

    @GetMapping("/platforms")
    fun platforms(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): List<PlatformMetricDto> =
        queryService?.getPlatformMetrics(from, to) ?: throw AnalyticsDisabledException()

    @GetMapping("/platforms/{platform}/trend")
    fun platformTrend(
        @PathVariable platform: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): PlatformTrendDto =
        queryService?.getPlatformTrend(platform, from, to) ?: throw AnalyticsDisabledException()

    @GetMapping("/kpis")
    fun kpis(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant
    ): KpiDashboardDto {
        return KpiDashboardDto(
            metrics = listOf(
                GrowthMetricDto("Revenue", 125000.0, 110000.0, 13.6),
                GrowthMetricDto("Users", 5400.0, 5000.0, 8.0),
                GrowthMetricDto("Conversion", 3.2, 2.9, 10.3)
            ),
            conversionRate = 3.2,
            ltv = BigDecimal.valueOf(150.0),
            cac = BigDecimal.valueOf(45.0)
        )
    }

    @GetMapping("/growth")
    fun growth(): Map<String, Any> = mapOf("growthRate" to 12.5, "churn" to 2.1)

    @GetMapping("/overview")
    fun overview(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant,
        @RequestParam(required = false) platform: String?
    ): OverviewDto =
        queryService?.getOverview(from, to, platform)
            ?: throw AnalyticsDisabledException()

    @GetMapping("/campaigns/{campaignId}/metrics")
    fun campaignMetrics(
        @PathVariable campaignId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant,
        @RequestParam(required = false, defaultValue = "hour") granularity: String
    ): CampaignMetricsDto =
        queryService?.getCampaignMetrics(campaignId, from, to, granularity)
            ?: throw AnalyticsDisabledException()
}
