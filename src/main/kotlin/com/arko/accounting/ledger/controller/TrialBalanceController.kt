package com.arko.accounting.ledger.controller

import com.arko.accounting.ledger.dto.TrialBalanceDto
import com.arko.accounting.ledger.service.TrialBalanceService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api")
class TrialBalanceController(
    private val service: TrialBalanceService
) {

    @GetMapping("/trial-balance")
    fun getTrialBalance(@RequestHeader(value = "X-Company-ID", required = false) companyId: UUID?): TrialBalanceDto {
        // Default to a random UUID if header is missing, since we are currently ignoring companyId in service
        val id = companyId ?: UUID.randomUUID()
        return service.generate(id)
    }
}
