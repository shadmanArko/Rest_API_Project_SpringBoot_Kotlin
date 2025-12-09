package com.arko.analytics.controller

import com.arko.analytics.dto.CampaignMetric
import com.arko.analytics.dto.DashboardOverview
import com.arko.analytics.service.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
class DashboardController(
    private val dashboardService: DashboardService
) {

    @GetMapping("/overview")
    fun getOverview(): ResponseEntity<DashboardOverview> {
        return ResponseEntity.ok(dashboardService.getOverview())
    }

    @GetMapping("/campaigns")
    fun getCampaigns(): ResponseEntity<List<CampaignMetric>> {
        return ResponseEntity.ok(dashboardService.getCampaignMetrics())
    }
}
