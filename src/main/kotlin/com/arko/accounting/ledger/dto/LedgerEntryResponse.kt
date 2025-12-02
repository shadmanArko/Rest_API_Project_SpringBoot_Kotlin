package com.arko.accounting.ledger.dto

import java.math.BigDecimal
import java.util.UUID

data class LedgerEntryResponse(
    val id: Long,
    val journalEntryId: UUID,
    val accountId: UUID,
    val debit: BigDecimal,
    val credit: BigDecimal
)
