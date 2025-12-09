package com.arko.accounting.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.math.BigDecimal

@FeignClient(name = "analytics-service", url = "http://localhost:8081/api/analytics")
interface AnalyticsClient {

    @PostMapping("/events")
    fun sendEvent(@RequestBody event: AnalyticsEventDto)
}

data class AnalyticsEventDto(
    val source: String,
    val campaignId: String? = null,
    val adId: String? = null,
    val eventType: String,
    val amount: BigDecimal? = null,
    val currency: String? = null
)
