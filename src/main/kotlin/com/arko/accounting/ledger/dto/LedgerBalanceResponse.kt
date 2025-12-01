package com.arko.accounting.ledger.dto

import java.math.BigDecimal
import java.util.UUID

data class LedgerBalanceResponse(
    val accountId: UUID,
    val debit: BigDecimal,
    val credit: BigDecimal,
    val netBalance: BigDecimal
)
