package com.arko.accounting.ledger.dto

import java.math.BigDecimal

data class TrialBalanceDto(
    val rows: List<TrialBalanceRowDto>,
    val totalDebit: BigDecimal,
    val totalCredit: BigDecimal,
    val isBalanced: Boolean
)
