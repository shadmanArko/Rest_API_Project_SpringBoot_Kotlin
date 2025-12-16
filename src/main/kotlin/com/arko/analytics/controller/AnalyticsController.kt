package com.arko.analytics.controller

import com.arko.analytics.dto.CampaignMetricsDto
import com.arko.analytics.dto.EventDto
import com.arko.analytics.dto.OverviewDto
import com.arko.analytics.service.AnalyticsQueryService
import com.arko.analytics.service.AnalyticsService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {
    @Autowired(required = false)
    private var queryService: AnalyticsQueryService? = null

    @PostMapping("/events")
    fun postEvent(@RequestBody @Valid dto: EventDto): ResponseEntity<Any> {
        analyticsService.ingest(dto)
        return ResponseEntity.accepted().body(mapOf("status" to "accepted", "eventId" to dto.eventId))
    }

    @GetMapping("/overview")
    fun overview(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime,
        @RequestParam(required = false) platform: String?
    ): OverviewDto {
        return queryService?.getOverview(from, to, platform)
            ?: throw IllegalStateException("ClickHouse analytics not enabled")
    }

    @GetMapping("/campaigns/{campaignId}/metrics")
    fun campaignMetrics(
        @PathVariable campaignId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: LocalDateTime,
        @RequestParam(required = false, defaultValue = "hour") granularity: String
    ): CampaignMetricsDto {
        return queryService?.getCampaignMetrics(campaignId, from, to, granularity)
            ?: throw IllegalStateException("ClickHouse analytics not enabled")
    }
}
