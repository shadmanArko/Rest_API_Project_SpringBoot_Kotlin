package com.arko.analytics.controller

import com.arko.analytics.dto.CampaignMetricsDto
import com.arko.analytics.dto.EventDto
import com.arko.analytics.dto.OverviewDto
import com.arko.analytics.exception.AnalyticsDisabledException
import com.arko.analytics.service.AnalyticsIngestService
import com.arko.analytics.service.AnalyticsQueryService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime,
        @RequestParam(required = false) platform: String?
    ): OverviewDto =
        queryService?.getOverview(from, to, platform)
            ?: throw AnalyticsDisabledException()

    @GetMapping("/campaigns/{campaignId}/metrics")
    fun campaignMetrics(
        @PathVariable campaignId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime,
        @RequestParam(required = false, defaultValue = "hour") granularity: String
    ): CampaignMetricsDto =
        queryService?.getCampaignMetrics(campaignId, from, to, granularity)
            ?: throw AnalyticsDisabledException()
}
