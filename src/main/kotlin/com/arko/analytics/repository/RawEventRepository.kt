package com.arko.analytics.repository

import com.arko.analytics.domain.RawEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import java.time.Instant

interface RawEventRepository : JpaRepository<RawEvent, UUID> {
    fun findAllByCampaignIdAndEventTimeBetween(campaignId: String, from: Instant, to: Instant): List<RawEvent>
}
