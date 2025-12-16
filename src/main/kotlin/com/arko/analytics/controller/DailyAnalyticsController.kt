package com.arko.analytics.controller

import com.arko.analytics.dto.TimePointDto
import com.arko.analytics.service.DailyAnalyticsQueryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/analytics/daily")
class DailyAnalyticsController {

    @Autowired(required = false)
    private var dailyQueryService: DailyAnalyticsQueryService? = null

    @GetMapping("/campaigns/{campaignId}")
    fun campaignDaily(
        @PathVariable campaignId: String,
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate
    ): List<TimePointDto> {
        return dailyQueryService?.campaignDailyMetrics(campaignId, from, to)
            ?: emptyList()
    }
}
