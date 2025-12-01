package com.arko.accounting.ledger.controller

import com.arko.accounting.ledger.dto.LedgerBalanceResponse
import com.arko.accounting.ledger.dto.LedgerEntryResponse
import com.arko.accounting.ledger.service.LedgerService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/ledger")
class LedgerController(
    private val service: LedgerService
) {

    @GetMapping("/entries")
    fun getEntries(): List<LedgerEntryResponse> =
        service.getLedgerEntries()

    @GetMapping("/entries/{accountId}", "/account/{accountId}")
    fun getEntriesForAccount(@PathVariable accountId: UUID): List<LedgerEntryResponse> =
        service.getLedgerForAccount(accountId)

    @GetMapping("/balances")
    fun getBalances(): List<LedgerBalanceResponse> =
        service.getBalances()
}
