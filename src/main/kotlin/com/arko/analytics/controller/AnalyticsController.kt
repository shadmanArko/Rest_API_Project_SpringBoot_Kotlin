package com.arko.analytics.controller

import com.arko.analytics.dto.CampaignMetricsDto
import com.arko.analytics.dto.CampaignOverviewDto
import com.arko.analytics.dto.DailyFinancialDto
import com.arko.analytics.dto.EventDto
import com.arko.analytics.dto.MonthlyPnLDto
import com.arko.analytics.dto.OverviewDto
import com.arko.analytics.exception.AnalyticsDisabledException
import com.arko.analytics.service.AnalyticsIngestService
import com.arko.analytics.service.AnalyticsQueryService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

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

    @GetMapping("/overview")
    fun overview(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) platform: String?
    ): OverviewDto =
        queryService?.getOverview(from.atStartOfDay(), to.atStartOfDay(), platform)
            ?: throw AnalyticsDisabledException()

    @GetMapping("/campaigns/{campaignId}/metrics")
    fun campaignMetrics(
        @PathVariable campaignId: String,
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false, defaultValue = "hour") granularity: String
    ): CampaignMetricsDto =
        queryService?.getCampaignMetrics(campaignId, from.atStartOfDay(), to.atStartOfDay(), granularity)
            ?: throw AnalyticsDisabledException()


    // ---------------- FINANCIAL KPIs ----------------

    @GetMapping("/financials/daily")
    fun dailyFinancials(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) companyId: String?
    ): List<DailyFinancialDto> =
        queryService?.getDailyFinancials(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), companyId)
            ?: throw AnalyticsDisabledException()


    @GetMapping("/financials/pnl/monthly")
    fun monthlyPnL(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) companyId: String?
    ): List<MonthlyPnLDto> =
        queryService?.getMonthlyPnL(from.atStartOfDay(), to.plusDays(1).atStartOfDay(), companyId)
            ?: throw AnalyticsDisabledException()


    @GetMapping("/campaigns/overview")
    fun campaignOverview(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate
    ): List<CampaignOverviewDto> =
        queryService?.getCampaignOverview(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
            ?: throw AnalyticsDisabledException()

}
