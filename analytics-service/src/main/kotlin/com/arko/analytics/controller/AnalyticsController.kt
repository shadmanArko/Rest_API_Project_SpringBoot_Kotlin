package com.arko.analytics.controller

import com.arko.analytics.dto.AnalyticsEvent
import com.arko.analytics.service.IngestionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val ingestionService: IngestionService
) {

    @PostMapping("/events")
    fun ingestEvent(@RequestBody event: AnalyticsEvent): ResponseEntity<String> {
        ingestionService.ingestEvent(event)
        return ResponseEntity.ok("Event Ingested")
    }
}
