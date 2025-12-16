package com.arko.analytics.worker

import com.arko.analytics.repository.RawEventRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AggregatorStub(
    private val rawRepo: RawEventRepository
) {
    private val log = LoggerFactory.getLogger(AggregatorStub::class.java)

    // Placeholder scheduled worker (runs every minute) that can be replaced with Kafka consumer or stream processor.
    @Scheduled(fixedDelay = 60_000)
    fun run() {
        val since = Instant.now().minusSeconds(60 * 5)
        val events = rawRepo.findAll().filter { it.ingestedAt.isAfter(since) }
        if (events.isNotEmpty()) {
            log.info("AggregatorStub saw ${events.size} recent events (example).")
        }
    }
}
