package com.arko.accounting.ledger.dto

import java.math.BigDecimal

data class TrialBalanceRowDto(
    val accountCode: String,
    val accountName: String,
    val debit: BigDecimal,
    val credit: BigDecimal
)
