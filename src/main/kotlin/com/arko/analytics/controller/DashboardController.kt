package com.arko.analytics.controller

import com.arko.analytics.assembler.DashboardAssembler
import com.arko.analytics.dto.dashboard.DashboardResponseDto
import com.arko.analytics.service.AnalyticsQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/analytics/dashboard")
class DashboardController(
    private val queryService: AnalyticsQueryService
) {

    @GetMapping
    fun getDashboard(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) companyId: String?
    ): DashboardResponseDto {

        val daily = queryService.getDailyFinancials(
            from.atStartOfDay(),
            to.plusDays(1).atStartOfDay(),
            companyId
        )

        val pnl = queryService.getMonthlyPnL(
            from.atStartOfDay(),
            to.plusDays(1).atStartOfDay(),
            companyId
        )

        val campaigns = queryService.getCampaignOverview(
            from.atStartOfDay(),
            to.plusDays(1).atStartOfDay()
        )

        return DashboardAssembler.assemble(daily, pnl, campaigns)
    }


}
